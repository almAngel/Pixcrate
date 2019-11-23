package alm.android.pixcrate.services;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Image;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
public interface ImageService {

    @Multipart
    @POST("/image/new")
    Call<DefaultResponse> upload(
            @Header("px-token") String token,
            @Part MultipartBody.Part image,
            @NotNull @Part("description") String description,
            @NotNull @Part("visibility") String visibility
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

    @PUT("/image/{id}")
    Call<DefaultResponse> edit(
            @Header("px-token") String token,
            @Path("id") String id,
            @Body Image image
    );
}
