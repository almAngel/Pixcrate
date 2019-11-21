package alm.android.pixcrate.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;

import alm.android.pixcrate.R;
import alm.android.pixcrate.adapters.PublicationAdapter;
import alm.android.pixcrate.events.OnFeedUpdateEventListener;
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
    private ArrayList<Image> images = new ArrayList<Image>();
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
    private ArrayList<Image> finalResponse = new ArrayList<Image>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
                    @Override
                    public Void call() {
                        mAdapter.setCollection(getResult());
                        return null;
                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }

        });

        imageService = RequestHelper.getImageService(getResources().getString(R.string.api_base));

        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        token = preferences.getString("access_token", "");

        layoutManager = new LinearLayoutManager(getContext());
        feedListView.setLayoutManager(layoutManager);

        mAdapter = new PublicationAdapter();
        feedListView.setAdapter(mAdapter);

        loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
            @Override
            public Void call() {
                mAdapter.setCollection(getResult());
                return null;
            }
        });

    }

    @Override
    public void onFeedUpdate() {
        Call<ArrayList<Image>> call = imageService.getAll(token);

        loadImagesAsync(new ResultFirer<ArrayList<Image>>() {
            @Override
            public Void call() {
                mAdapter.setCollection(getResult());
                return null;
            }
        });
    }

    public void loadImagesAsync(@Nullable ResultFirer<ArrayList<Image>> doAfter) {
        Call<ArrayList<Image>> call = imageService.getAll(token);

        call.enqueue(new Callback<ArrayList<Image>>() {
            @Override
            public void onResponse(Call<ArrayList<Image>> call, Response<ArrayList<Image>> response) {
                doAfter.setResult(response.body());
                doAfter.call();
            }

            @Override
            public void onFailure(Call<ArrayList<Image>> call, Throwable t) {

            }
        });
    }

}

