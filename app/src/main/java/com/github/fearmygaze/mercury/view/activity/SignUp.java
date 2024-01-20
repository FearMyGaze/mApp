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
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.firebase.interfaces.SignCallBackResponse;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    MaterialToolbar toolbar;
    TextInputLayout emailError, usernameError, passwordError;
    TextInputEditText email, username, password;
    MaterialButton createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = findViewById(R.id.signUpToolBar);
        emailError = findViewById(R.id.signUpEmailError);
        email = findViewById(R.id.signUpEmail);
        passwordError = findViewById(R.id.signUpPasswordError);
        username = findViewById(R.id.signUpUsername);
        usernameError = findViewById(R.id.signUpUsernameError);
        password = findViewById(R.id.signUpPassword);
        createAccount = findViewById(R.id.signUpCreateAccount);

        toolbar.setNavigationOnClickListener(v -> finish());

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
            if (!emailError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignUp.this);
                View dialogView = LayoutInflater.from(SignUp.this).inflate(R.layout.dialog_sign_up, null);
                builder.setView(dialogView)
                        .setTitle(R.string.signUpDialogTitle)
                        .setMessage(R.string.signUpDialogMessage)
                        .setCancelable(false);
                AlertDialog dialog = builder.show();
                new UserActions(v.getContext()).signUp(sEmail, sUsername, sPassword, new SignCallBackResponse<String>() {
                    @Override
                    public void onSuccess(String object) {
                        dialog.dismiss();
                        Toast.makeText(SignUp.this, object, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUp.this, Main.class));
                        finish();
                    }

                    @Override
                    public void onError(int error, String message) {
                        dialog.dismiss();
                        switch (error) {
                            case 1:
                                Tools.setErrorToLayout(emailError, message, true);
                                email.requestFocus();
                                break;
                            case 2:
                                Tools.setErrorToLayout(usernameError, message, true);
                                username.requestFocus();
                                break;
                            case 3:
                                Tools.setErrorToLayout(passwordError, message, true);
                                password.requestFocus();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        dialog.dismiss();
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, Main.class));
                        finish();
                    }
                });
            }
        });
    }

}
