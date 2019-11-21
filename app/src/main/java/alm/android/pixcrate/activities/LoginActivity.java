package alm.android.pixcrate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;

import com.auth0.android.jwt.JWT;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import alm.android.pixcrate.R;
import alm.android.pixcrate.pojos.User;
import alm.android.pixcrate.services.AuthService;
import alm.android.pixcrate.tools.RequestHelper;
import alm.android.pixcrate.tools.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import alm.android.pixcrate.pojos.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loginButton)
    protected Button loginButton;
    @BindView(R.id.registerButton)
    protected Button registerButton;
    @BindView(R.id.emailInput)
    protected TextInputLayout emailInput;
    @BindView(R.id.passwordInput)
    protected TextInputLayout passwordInput;

    private AuthService authService;
    private Context thisContext;
    private String sbMessage;
    private Pattern emailPattern;
    private Pattern passwordPattern;
    private SharedPreferences preferences;
    private static final int REGISTERED_CODE = 201;
    private static final int LOGGED_OUT_CODE = 11239;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // GET SharedPreferences
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        passwordInput.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());


        // REGEX VALIDATION INIT
        emailPattern = Pattern.compile(getResources().getString(R.string.email_regex));
        passwordPattern = Pattern.compile(getResources().getString(R.string.password_regex));

        thisContext = this;

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailAux = emailInput.getEditText().getText().toString().trim();
                        String passwordAux = passwordInput.getEditText().getText().toString().trim();

                        if (emailAux.equals("")) {
                            sbMessage = "Email field is missing";
                        } else if (passwordAux.equals("")) {
                            sbMessage = "Password field is missing";
                        } else {
                            Boolean isValidEmail = emailPattern.matcher(emailAux).matches();
                            Boolean isValidPassword = passwordPattern.matcher(passwordAux).matches();

                            if (isValidEmail && isValidPassword) {
                                sbMessage = "Login in...";
                                Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();

                                authService = RequestHelper.getHomeService(getResources().getString(R.string.api_base));

                                User u = new User(
                                        emailAux,
                                        passwordAux
                                );

                                Call<Token> call = authService.getAccess(u);
                                call.enqueue(new Callback<Token>() {
                                    @Override
                                    public void onResponse(Call<Token> call, Response<Token> response) {
                                        //Log.d("Token: ", response.body().toString());
                                        if (response.body().status == 200) {
                                            preferences.edit().putString("access_token", response.body().access_token).apply();
                                            // Navigate to HomeActivity
                                            Intent goHome = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivityForResult(goHome, 11239);
                                            finish();
                                            return;
                                        } else if (response.body().status == 404 || response.body().status == 401) {
                                            sbMessage = "Error: Invalid credentials";
                                            Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Token> call, Throwable t) {
                                        Snackbar.make(v, "No internet connection", BaseTransientBottomBar.LENGTH_LONG).show();
                                    }
                                });

                            } else if (isValidEmail && !isValidPassword) {
                                sbMessage = "Error: Password must contain minimum 8 characters, an uppercase, a lowercase and one number";
                            } else if (!isValidEmail && isValidPassword) {
                                sbMessage = "Error: Email must be a valid email";
                            } else {
                                sbMessage = "Error: Both email and password field not valid";
                            }
                        }
                        Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }
        );

        registerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivityForResult(goRegister, REGISTERED_CODE);
                    }
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REGISTERED_CODE:
                if (resultCode == REGISTERED_CODE) {
                    Snackbar.make(loginButton, "User registered", Snackbar.LENGTH_LONG).show();
                }
                else {
                    Snackbar.make(loginButton, "User not registered", Snackbar.LENGTH_LONG).show();
                }
                break;
            case LOGGED_OUT_CODE:
                if(resultCode == LOGGED_OUT_CODE) {
                    Snackbar.make(loginButton, "User logged out", Snackbar.LENGTH_LONG).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
