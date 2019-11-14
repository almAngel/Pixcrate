package alm.android.pixcrate.tools;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import alm.android.pixcrate.services.AuthService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestHelper {

    private static AuthService service = null;
    private static OkHttpClient httpClient = null;
    private static Gson gsonParser = null;
    private static Retrofit retrofit = null;
    private static HttpLoggingInterceptor interceptor = null;

    // Prevent outter access to instance
    private RequestHelper() {
    }

    public static AuthService getHomeService(String url) {

        // Only if our Singleton instance is not created yet
        if (service == null) {

            // Interceptor is only for debug purposes
            interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String response) {
                    Log.d("INTERCEPTOR:", response);
                }
            });

            // Set interceptor level for logging
            interceptor.level(HttpLoggingInterceptor.Level.HEADERS);

            // Specifying HttpClient we need a debug logger
            httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            // Creating a parser for body responses
            gsonParser = new GsonBuilder().create();

            //Building retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gsonParser))
                    .build();

            // This is our Singleton instance
            service = retrofit.create(AuthService.class);
        }
        return service;
    }

}
