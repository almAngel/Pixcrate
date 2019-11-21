package alm.android.pixcrate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import alm.android.pixcrate.R;
import alm.android.pixcrate.pojos.DBResponse;
import alm.android.pixcrate.pojos.User;
import alm.android.pixcrate.services.AuthService;
import alm.android.pixcrate.tools.RequestHelper;
import alm.android.pixcrate.tools.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_loginButton)
    protected Button loginButton;
    @BindView(R.id.register_registerButton)
    protected Button registerButton;
    @BindView(R.id.register_emailInput)
    protected TextInputLayout emailInput;
    @BindView(R.id.register_usernameInput)
    protected TextInputLayout usernameInput;
    @BindView(R.id.register_PasswordInput)
    protected TextInputLayout passwordInput;
    @BindView(R.id.register_confirmPasswordInput)
    protected TextInputLayout confirmPasswordInput;

    private AuthService authService;
    private Context thisContext;
    private String sbMessage;
    private Pattern emailPattern;
    private Pattern usernamePattern;
    private Pattern passwordPattern;
    private SharedPreferences preferences;
    private boolean isCorrectEmail,
            isCorrectUsername,
            isCorrectPassword,
            isCorrectPasswordConfirm,
            samePassword;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        // GET SharedPreferences
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        passwordInput.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPasswordInput.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());

        /*
        //Check if token expired, then logout
        SessionManager.destroySessionOnExpire(this, preferences);
        */

        thisContext = this;

        String emailAux = emailInput.getEditText().getText().toString().trim();
        String usernameAux = usernameInput.getEditText().getText().toString().trim();
        String passwordAux = passwordInput.getEditText().getText().toString().trim();
        String confirmPasswordAux = confirmPasswordInput.getEditText().getText().toString().trim();

        emailPattern = Pattern.compile(getResources().getString(R.string.email_regex));
        usernamePattern = Pattern.compile(getResources().getString(R.string.username_regex));
        passwordPattern = Pattern.compile(getResources().getString(R.string.password_regex));

        /**
         *
         * CHECK INPUT VALUES FOR HELPTEXT MESSAGE
         */

        if (emailAux.equals("")) {
            emailInput.setHelperText("Must fill email input");
        }

        emailInput.getEditText().addTextChangedListener(new TextWatcher() {

            private Boolean matches;

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                matches = emailPattern.matcher(s.toString().trim()).matches();

                if (s.toString().equals("")) {
                    emailInput.setHelperText("Must fill email input");
                } else {
                    if (matches) {
                        emailInput.setHelperText("");
                        isCorrectEmail = true;
                    } else {
                        emailInput.setHelperText("Email is not a valid email");
                        isCorrectEmail = false;
                    }
                }
            }
        });

        if (usernameAux.equals("")) {
            usernameInput.setHelperText("Must fill username input");
        }

        usernameInput.getEditText().addTextChangedListener(new TextWatcher() {

            private Boolean matches;

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                matches = usernamePattern.matcher(s.toString().trim()).matches();

                if (s.toString().equals("")) {
                    usernameInput.setHelperText("Must fill username input");
                } else {
                    if (matches) {
                        usernameInput.setHelperText("");
                        isCorrectUsername = true;
                    } else {
                        usernameInput.setHelperText("Username is not a valid username");
                        isCorrectUsername = false;
                    }
                }
            }
        });

        if (passwordAux.equals("")) {
            passwordInput.setHelperText("Must fill password input");
        }

        passwordInput.getEditText().addTextChangedListener(new TextWatcher() {

            private Boolean matches;

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                matches = passwordPattern.matcher(s.toString().trim()).matches();

                if (s.toString().equals("")) {
                    passwordInput.setHelperText("Must fill password input");
                } else {
                    if (matches) {
                        passwordInput.setHelperText("");
                        isCorrectPassword = true;
                    } else {
                        passwordInput.setHelperText("Password is not a valid password");
                        isCorrectPassword = false;
                    }
                }
            }
        });

        if (confirmPasswordAux.equals("")) {
            confirmPasswordInput.setHelperText("Must fill password confirmation input");
        }

        confirmPasswordInput.getEditText().addTextChangedListener(new TextWatcher() {

            private Boolean matches;

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                matches = passwordPattern.matcher(s.toString().trim()).matches();

                if (s.toString().equals("")) {
                    confirmPasswordInput.setHelperText("Must fill password confirmation input");
                } else {
                    if (matches) {
                        confirmPasswordInput.setHelperText("");
                        isCorrectPasswordConfirm = true;
                    } else {
                        confirmPasswordInput.setHelperText("Password is not a valid password");
                        isCorrectPasswordConfirm = false;
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCorrectEmail && isCorrectPassword && isCorrectUsername && isCorrectPasswordConfirm) {

                    String emailAux = emailInput.getEditText().getText().toString().trim();
                    String usernameAux = usernameInput.getEditText().getText().toString().trim();
                    String passwordAux = passwordInput.getEditText().getText().toString().trim();
                    String confirmPasswordAux = confirmPasswordInput.getEditText().getText().toString().trim();

                    if (passwordAux.equals(confirmPasswordAux)) {
                        sbMessage = "Signing up...";

                        authService = RequestHelper.getHomeService(getResources().getString(R.string.api_base));

                        User u = new User(
                                null,
                                emailAux,
                                usernameAux,
                                passwordAux,
                                null
                        );

                        Call<DBResponse> call = authService.register(u);
                        call.enqueue(new Callback<DBResponse>() {
                            @Override
                            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                                if (response.body().getStatus() == 201) {
                                    // Navigate to LoginActivity
                                    setResult(response.body().getStatus());
                                    finish();
                                    return;
                                } else if(response.body().getStatus() == 409) {
                                    sbMessage = "Error: User already exists";
                                    Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();
                                } else if (response.body().getStatus() == 422) {
                                    sbMessage = "Error: Missing required information";
                                    Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DBResponse> call, Throwable t) {
                            }
                        });

                    } else {
                        sbMessage = "Error: Confirmation password value differs from final password value";
                    }
                } else {
                    sbMessage = "Error: Missing required information";
                }
                Snackbar.make(v, sbMessage, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(goLogin);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // GET SharedPreferences
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        if (!preferences.getString("access_token", "").equals("")) {
            String token = preferences.getString("access_token", "");
            JWT parsedJWT = new JWT(token);
            boolean isExpired = parsedJWT.isExpired(10);

            if (!isExpired) {
                Intent goHome = new Intent(RegisterActivity.this, HomeActivity.class);
                RegisterActivity.this.startActivity(goHome);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
