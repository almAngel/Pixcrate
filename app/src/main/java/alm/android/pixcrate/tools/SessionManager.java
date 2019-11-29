package alm.android.pixcrate.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.HomeActivity;
import alm.android.pixcrate.activities.LoginActivity;
import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Token;
import alm.android.pixcrate.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {

    private static AuthService authService;

    public static void destroySessionOnExpire(Context context, SharedPreferences preferences, Class<?> goTo) {
        Intent logOut = new Intent(context, goTo);

        if(!preferences.getString("access_token", "").equals("")) {
            String token = preferences.getString("access_token", "");
            JWT parsedJWT = new JWT(token);
            boolean isExpired = parsedJWT.isExpired(10);

            if(isExpired) {
                logOut.putExtra("info", "Logged out");
                context.startActivity(logOut);
            }
        } else {
            logOut.putExtra("info", "Logged out");
            context.startActivity(logOut);
        }
    }

    public static void destroySession(Activity activity, SharedPreferences preferences) {

        String token = preferences.getString("access_token", "");

        authService = RequestHelper.getHomeService(activity.getResources().getString(R.string.api_base));

        Call<DefaultResponse> call = authService.logout(token);

        preferences.edit().putString("access_token", "").apply();
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if(response.body().getStatus() == 200) {
                    // Navigate to HomeActivity
                    Intent logOut = new Intent(activity, LoginActivity.class);

                    activity.setResult(11239);
                    activity.finish();
                    activity.startActivity(logOut);

                    return;
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Snackbar.make(new View(HomeActivity.settingsFragment.getContext()), t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
        return;
    }
}
