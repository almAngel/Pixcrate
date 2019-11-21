package alm.android.pixcrate.services;

import java.util.ArrayList;

import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Image;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageService {

    @POST("/image/new")
    Call<DefaultResponse> upload(
            @Header("px-token") String token,
            @Part("image") MultipartBody.Part image,
            @Part("description") RequestBody description,
            @Part("visibility") RequestBody visibility
    );

    @GET("/image/all")
    Call<ArrayList<Image>> getAll(
            @Header("px-token") String token
    );

    @DELETE("/image/{id}")
    Call<DefaultResponse> delete(
            @Header("px-token") String token,
            @Path("id") String id
    );
}
