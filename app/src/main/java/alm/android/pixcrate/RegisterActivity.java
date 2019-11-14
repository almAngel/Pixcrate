package alm.android.pixcrate;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;
    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private Context thisContext;
    private String sbMessage;
    private Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z.]+[a-z]");
    private Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        thisContext = this;


    }
}
