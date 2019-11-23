package alm.android.pixcrate.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.HomeActivity;
import alm.android.pixcrate.adapters.PublicationAdapter;
import alm.android.pixcrate.events.OnFeedUpdateEventListener;
import alm.android.pixcrate.events.UpdatePulsator;
import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Image;
import alm.android.pixcrate.services.ImageService;
import alm.android.pixcrate.tools.ResultFirer;
import alm.android.pixcrate.tools.RequestHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedFragment extends Fragment implements OnFeedUpdateEventListener {

    private View fragmentView;
    private SharedPreferences preferences;
    private ImageService imageService;
    private String token;

    @BindView(R.id.feed_publicationList)
    protected RecyclerView feedListView;

    @BindView(R.id.feed_swipeLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.feed_nestedScroll)
    protected NestedScrollView nestedScrollView;

    private PublicationAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageService = RequestHelper.getImageService(getResources().getString(R.string.api_base));

        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        token = preferences.getString("access_token", "");

        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        feedListView.setLayoutManager(layoutManager);

        mAdapter = new PublicationAdapter();
        feedListView.setAdapter(mAdapter);

        // POPULATE ADAPTER
        loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
            @Override
            public Void call() {
                mAdapter.setCollection(getResult());
                return null;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
                    @Override
                    public Void call() {
                        //mAdapter.modifyItem((Image)args[1], (Integer) args[2]);
                        mAdapter.setCollection(getResult());
                        return null;
                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }

        });

    }

    public void loadImagesAsync(@Nullable ResultFirer<ArrayList<Image>> doAfter) {
        Call<ArrayList<Image>> call = imageService.getAll(token);

        call.enqueue(new Callback<ArrayList<Image>>() {
            @Override
            public void onResponse(Call<ArrayList<Image>> call, Response<ArrayList<Image>> response) {
                try {
                    doAfter.setResult(response.body());
                    doAfter.call();
                } catch (NullPointerException e) {
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Image>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onFeedUpdate(Object... args) {

        if ((Integer) args[0] == 9097) {
            // REPOPULATE ADAPTER ON UPLOAD
            loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
                @Override
                public Void call() {
                    mAdapter.addItem(getResult().get(getResult().size()-1));
                    return null;
                }
            });
        } else if ((Integer) args[0] == 7865) {
            PublicationAdapter.PublicationViewHolder holder =
                    (PublicationAdapter.PublicationViewHolder) feedListView.findViewHolderForAdapterPosition((Integer) args[2]);

            EditText descriptionEditText = holder.itemView.findViewById(R.id.publication_description);

            descriptionEditText.setEnabled(true);
            descriptionEditText.requestFocus();
            descriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

            View v = getActivity().getCurrentFocus();

            descriptionEditText.setSelection(descriptionEditText.getText().length());

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            nestedScrollView.scrollTo(0, descriptionEditText.getBottom());

            descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Image img = (Image) args[1];
                        img.setDescription(descriptionEditText.getText().toString().trim());
                        Call<DefaultResponse> editCall = imageService.edit(token, img.getId(), img);

                        editCall.enqueue(new Callback<DefaultResponse>() {
                            @Override
                            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                if (response.body().getStatus() == 200) {
                                    mAdapter.modifyItem(img, (Integer) args[2]);
                                    descriptionEditText.setEnabled(false);
                                    descriptionEditText.clearFocus();
                                    nestedScrollView.fullScroll(View.FOCUS_UP);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);


                                    //SEND ANOTHER PULSE TO ITSELF (WE NEED TO PASS IN THE RESULTS AGAIN
                                    new UpdatePulsator().addListener((FeedFragment) HomeActivity.homeFragment).emitPulse(
                                            args[0], img, args[2], 200, img.getDescription());
                                    return;
                                } else {
                                    Snackbar.make(feedListView, response.body().getMsg(), BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                System.out.println(t);
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });

            //CHECK IF KEYBOARD IS HIDDEN
            descriptionEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                String lastText = descriptionEditText.getText().toString();

                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    descriptionEditText.getWindowVisibleDisplayFrame(r);
                    int screenHeight = descriptionEditText.getRootView().getHeight();

                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        if (args[3] != null && (Integer) args[3] == 200) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                        // IF USER CHANGES MENU TAB
                        else if(HomeActivity.activeFragment.getClass().getName() != FeedFragment.class.getName()) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }

                    } else {
                        //JUST TO CONTROL EVERYTIME WE DO AN ACTION WITHIN
                        if (args[3] != null && (Integer) args[3] != 200) {
                            descriptionEditText.setText(lastText);
                        } else if (args[3] != null && (Integer) args[3] == 200) {
                            descriptionEditText.setText((String) args[4]);
                        } else {
                            descriptionEditText.setText(lastText);
                        }
                        descriptionEditText.setEnabled(false);
                        descriptionEditText.clearFocus();
                    }

                }
            });

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
                    @Override
                    public Void call() {
                        mAdapter.modifyItem((Image)args[1], (Integer) args[2]);
                        //mAdapter.setCollection(getResult());
                        return null;
                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }

        });

    }

}

