package alm.android.pixcrate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.fragments.FeedFragment;
import alm.android.pixcrate.activities.fragments.SettingsFragment;
import alm.android.pixcrate.activities.fragments.UploadFragment;
import alm.android.pixcrate.tools.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.bottomNav)
    protected BottomNavigationView bottomNav;

    public static final Fragment homeFragment = new FeedFragment();
    public static final Fragment uploadFragment = new UploadFragment();
    public static final Fragment settingsFragment = new SettingsFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;
    private SharedPreferences preferences;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // GET SharedPreferences
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        //Check if token expired, then logout
        SessionManager.destroySessionOnExpire(this, preferences, LoginActivity.class);

        fm.beginTransaction().add(R.id.frameLayout, settingsFragment, "3").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.frameLayout, uploadFragment, "2").hide(uploadFragment).commit();
        fm.beginTransaction().add(R.id.frameLayout, homeFragment, "1").commit();
        bottomNav.setSelectedItemId(R.id.homeNavButton);

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.homeNavButton:
                                fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                                activeFragment = homeFragment;
                                return true;

                            case R.id.uploadNavButton:
                                fm.beginTransaction().hide(activeFragment).show(uploadFragment).commit();
                                activeFragment = uploadFragment;
                                return true;

                            case R.id.profileNavButton:
                                //fm.beginTransaction().hide(activeFragment).show(fragment3).commit();
                                //active = fragment3;
                                return true;

                            case R.id.settingsNavButton:
                                fm.beginTransaction().hide(activeFragment).show(settingsFragment).commit();
                                activeFragment = settingsFragment;
                                return true;

                        }
                        return false;
                    }
                }
        );

    }

}
