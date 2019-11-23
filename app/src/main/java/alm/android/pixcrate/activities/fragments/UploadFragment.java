package alm.android.pixcrate.activities.fragments;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.net.URLConnection;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.HomeActivity;
import alm.android.pixcrate.adapters.PublicationAdapter;
import alm.android.pixcrate.events.UpdatePulsator;
import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Image;
import alm.android.pixcrate.services.ImageService;
import alm.android.pixcrate.tools.RequestHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFragment extends Fragment {

    @BindView(R.id.upload_uploadButton)
    protected CardView uploadButton;

    @BindView(R.id.upload_progressBar)
    protected ProgressBar progressBar;

    private View fragmentView;
    private SharedPreferences preferences;
    private ImageService imageService;
    private String token;
    private PublicationAdapter mAdapter;

    private final int RESULT_LOAD_IMAGE = 1123;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_upload, container, false);
        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //GET IMAGE FROM STORAGE
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });

        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        token = preferences.getString("access_token", "");

    }


    public void getImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
            progressBar.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            File nativeFile = new File(picturePath);
            String mimeType = URLConnection.guessContentTypeFromName(nativeFile.getName());

            RequestBody fileToUpload = RequestBody.create(nativeFile, MediaType.parse(mimeType));;

            imageService = RequestHelper.getScalarImageService(getResources().getString(R.string.api_base));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", nativeFile.getName(), fileToUpload);

            Call<DefaultResponse> call = imageService.upload(token, body, " " , "public");

            UpdatePulsator pulsator = new UpdatePulsator();
            FeedFragment fragment = (FeedFragment) HomeActivity.homeFragment;

            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if(response.body().getStatus() == 201) {
                        pulsator.addListener(fragment);
                        pulsator.emitPulse(9097);
                        Snackbar.make(fragmentView, response.body().getMsg(), BaseTransientBottomBar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        Snackbar.make(fragmentView, response.body().getMsg(), BaseTransientBottomBar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    Snackbar.make(fragmentView, t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                    Log.e("ERROR", t.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                    t.printStackTrace();
                }
            });


        }

    }
}
