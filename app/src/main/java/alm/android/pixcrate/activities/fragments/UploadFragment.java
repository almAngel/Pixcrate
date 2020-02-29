package alm.android.pixcrate.activities.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.net.URLConnection;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.HomeActivity;
import alm.android.pixcrate.adapters.PublicationAdapter;
import alm.android.pixcrate.events.UpdatePulsator;
import alm.android.pixcrate.events.UploadObservable;
import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Image;
import alm.android.pixcrate.services.ImageService;
import alm.android.pixcrate.tools.CameraUtils;
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

    @BindView(R.id.openCamera)
    protected ImageButton openCameraButton;

    private View fragmentView;
    private SharedPreferences preferences;
    private ImageService imageService;
    private String token;
    private PublicationAdapter mAdapter;
    private Context fragmentContext;

    private final int CAMERA_PERMISSION = 5446;
    private final int TOOK_PICTURE = 3483;

    private final int RESULT_LOAD_IMAGE = 1123;

    private static File file;
    private static Uri fileUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_upload, container, false);
        ButterKnife.bind(this, fragmentView);
        fragmentContext = getContext();

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

        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    /*
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String fileName = "IMG_" + timeStamp.toString() + ".jpg";
                    File rootDir = fragmentContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File preFile = new File(rootDir, fileName);
                     */

                    file = CameraUtils.getOutputMediaFile(0);
                    fileUri =  FileProvider.getUriForFile(fragmentContext,
                            fragmentContext.getApplicationContext().getPackageName() + ".provider",
                            file);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivityForResult(cameraIntent, TOOK_PICTURE);
                }

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

                        Image img = new Image("", "", "", "public");
                        pulsator.addListener(fragment);
                        pulsator.emitPulse(9097, img);
                        UploadObservable.get_instance().inform();

                        // Replaced by notification system
                        // Snackbar.make(fragmentView, response.body().getMsg(), BaseTransientBottomBar.LENGTH_LONG).show();

                        uploadNotification(response.body().getMsg());

                        progressBar.setVisibility(View.INVISIBLE);
                    } else {

                        // Replaced by notification system
                        // Snackbar.make(fragmentView, response.body().getMsg(), BaseTransientBottomBar.LENGTH_LONG).show();
                        uploadNotification(response.body().getMsg());
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

        if (requestCode == TOOK_PICTURE && resultCode == Activity.RESULT_OK) {

            String pathToInternallyStoredImage = CameraUtils.saveToInternalStorage(fragmentContext, fileUri, 0);
            fragmentContext.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(file)
                    )
            );
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadNotification(String messageContent) {
        NotificationManager notificationManager = (NotificationManager) fragmentContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("UPLOAD_CHANNEL",
                    "UPLOAD_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(fragmentContext, "UPLOAD_CHANNEL")
                .setSmallIcon(R.drawable.ic_add_a_photo_black_24dp)
                .setContentTitle("Image upload")
                .setContentText(messageContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(fragmentContext, HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(fragmentContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pi);
        notificationManager.notify(0, builder.build());
    }
}

