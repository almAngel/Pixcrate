package alm.android.pixcrate.services;

import alm.android.pixcrate.pojos.DBResponse;
import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.User;
import alm.android.pixcrate.pojos.Token;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/home/access")
    Call<Token> getAccess(@Body User inputUser);

    @POST("/home/new")
    Call<DBResponse> register(@Body User inputUser);

    @DELETE("/home/end")
    Call<DefaultResponse> logout(
            @Header("px-token") String token
    );
}
