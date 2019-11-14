package alm.android.pixcrate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.auth0.android.jwt.JWT;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import alm.android.pixcrate.pojos.User;
import alm.android.pixcrate.services.AuthService;
import alm.android.pixcrate.tools.RequestHelper;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // GET SharedPreferences
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        if(!preferences.getString("access_token", "").equals("")) {
            String token = preferences.getString("access_token", "");
            JWT parsedJWT = new JWT(token);
            boolean isExpired = parsedJWT.isExpired(10);

            if(!isExpired) {
                Intent goRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(goRegister);
            }
        }

        // REGEX VALIDATION INIT
        emailPattern = Pattern.compile(getResources().getString(R.string.email_regex));
        passwordPattern = Pattern.compile(getResources().getString(R.string.password_regex));
        //passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

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
                                Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_INDEFINITE).show();

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
                                            //System.out.println(preferences.getString("access_token", ""));
                                        } else {
                                            sbMessage = "User not found";
                                            Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Token> call, Throwable t) {
                                        Snackbar.make(v, t.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
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
                        LoginActivity.this.startActivity(goRegister);
                    }
                }
        );

    }
}
