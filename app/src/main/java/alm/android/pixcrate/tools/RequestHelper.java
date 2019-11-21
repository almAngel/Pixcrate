package alm.android.pixcrate.tools;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import alm.android.pixcrate.services.AuthService;
import alm.android.pixcrate.services.ImageService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestHelper {

    /**
     *  SERVICE INSTANCES
     */
    private static AuthService authService = null;
    private static ImageService imageService = null;

    private static OkHttpClient httpClient = null;
    private static Gson gsonParser = null;
    private static Retrofit retrofit = null;
    private static HttpLoggingInterceptor interceptor = null;

    // Prevent outter access to instance
    private RequestHelper() {
    }

    public static AuthService getHomeService(String url) {

        // Only if our Singleton instance is not created yet
        if (authService == null) {

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
            authService = retrofit.create(AuthService.class);
        }
        return authService;
    }

    public static ImageService getImageService(String url) {

        // Only if our Singleton instance is not created yet
        if (imageService == null) {

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
            imageService = retrofit.create(ImageService.class);
        }
        return imageService;
    }

}
