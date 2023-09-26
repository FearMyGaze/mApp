package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    MaterialToolbar toolbar;
    TextInputLayout usernameError, emailError, passwordError;
    TextInputEditText username, email, password;
    MaterialButton createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = findViewById(R.id.signUpToolBar);
        usernameError = findViewById(R.id.signUpUsernameError);
        username = findViewById(R.id.signUpUsername);
        emailError = findViewById(R.id.signUpEmailError);
        email = findViewById(R.id.signUpEmail);
        passwordError = findViewById(R.id.signUpPasswordError);
        password = findViewById(R.id.signUpPassword);
        createAccount = findViewById(R.id.signUpCreateAccount);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameError.setEnabled(RegEx.isEmailValid(email, emailError, SignUp.this));
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordError.setEnabled(RegEx.isUsernameValid(username, usernameError, SignUp.this));
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                createAccount.setEnabled(RegEx.isPasswordValid(password, passwordError, SignUp.this));
            }
        });

        createAccount.setOnClickListener(v -> {
            String sEmail = Objects.requireNonNull(email.getText()).toString().trim();
            String sUsername = Objects.requireNonNull(username.getText()).toString().trim();
            String sPassword = Objects.requireNonNull(password.getText()).toString().trim();
            if (!emailError.isErrorEnabled() && !usernameError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this, R.style.customAlertDialog);
                View dialogView = LayoutInflater.from(SignUp.this).inflate(R.layout.dialog_sign_up, null);
                builder.setView(dialogView)
                        .setTitle(R.string.signUpDialogTitle)
                        .setMessage(R.string.signUpDialogMessage)
                        .setCancelable(false);
                AlertDialog dialog = builder.show();
                Auth.validateDataAndCreateUser(sUsername, sEmail, sPassword, SignUp.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        dialog.dismiss();
                        switch (code) {
                            case 0:
                                Toast.makeText(SignUp.this, "We sent at your email a verification link, To continue please activate your account", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, SignIn.class));
                                finish();
                                break;
                            case 1:
                                Tools.setErrorToLayout(emailError, getString(R.string.authEmail), true);
                                email.requestFocus();
                                break;
                            case 2:
                                Tools.setErrorToLayout(usernameError, getString(R.string.authUsername), true);
                                username.requestFocus();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        dialog.dismiss();
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
