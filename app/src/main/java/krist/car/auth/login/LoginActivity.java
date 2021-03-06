package krist.car.auth.login;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import krist.car.MainActivity;
import krist.car.api.FirebaseApiInterface;
import krist.car.auth.AuthRepo;
import krist.car.models.LoginFormModel;
import krist.car.R;
import krist.car.auth.register.RegisterActivity;
import krist.car.utils.Constants;
import krist.car.utils.Helpers;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button logIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView registerButton;
    private LoginViewModel loginViewModel;
    private String userName, password;

    @Inject
    FirebaseApiInterface api;

    @Inject
    AuthRepo repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_insta_layout);

        editTextEmail = findViewById(R.id.emailLogIn);
        editTextPassword =  findViewById(R.id.passwordLogIn);
        logIn =  findViewById(R.id.buttonLogIn);
        registerButton = findViewById(R.id.register_button);

        logIn.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        initLoginViewModel();

    }

    private void initLoginViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void attemptLogin() {
        userName = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        userName = "b@b.com";
        password = "Admin123!";

        if(!validateCredentials()) {
            //return;
        }

        loginViewModel.signInWithEmailAndPassword(buildLoginRequest());
        loginViewModel.isLoginSuccess().observe(this, isSuccess -> {
            if(isSuccess) {
                Helpers.goToActivity(this, MainActivity.class);
                Helpers.showToastMessage(LoginActivity.this, "Hyrje e sukseshme");
            }else {
                Bundle bundle = new Bundle();
                bundle.putString("email", userName);
                bundle.putString("password", password);

                Helpers.goToActivityAttachBundle(LoginActivity.this, RegisterActivity.class, "auth", bundle);
                Helpers.showToastMessage(LoginActivity.this, "Regjistrimi i metejshem");
            }
        });
    }

    private LoginFormModel buildLoginRequest() {
        return new LoginFormModel(userName, password);
    }

    private Boolean validateCredentials() {
        if(!Helpers.validateStringBaseOnRegex(userName, Constants.EMAIL_REGEX)) {
            editTextEmail.setError("Plotesoni fushen ne formatin e duhur");
            return false;
        }else if(!Helpers.validateStringBaseOnRegex(password, Constants.PASSWORD_REGEX)) {
            editTextPassword.setError("Plotesoni fushen ne formatin e duhur");
            return false;
        }else {
            return true;
        }
    }

    private void goToRegisterActivity() {
        Helpers.goToActivity(this, RegisterActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogIn:
                attemptLogin();
                break;
            case R.id.register_button:
                goToRegisterActivity();
                break;
        }
    }
}
