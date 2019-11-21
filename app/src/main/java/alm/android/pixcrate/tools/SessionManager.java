package alm.android.pixcrate.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;

import alm.android.pixcrate.activities.LoginActivity;

public class SessionManager {

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
        preferences.edit().putString("access_token", "").apply();
        // Navigate to HomeActivity
        Intent logOut = new Intent(activity, LoginActivity.class);

        activity.setResult(11239);
        activity.finish();
        activity.startActivity(logOut);
        return;
    }
}
