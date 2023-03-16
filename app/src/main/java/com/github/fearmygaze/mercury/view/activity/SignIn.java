package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    TextInputLayout credentialError, passwordError;
    TextInputEditText credential, password;
    MaterialButton signIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        credentialError = findViewById(R.id.signInCredentialsError);
        passwordError = findViewById(R.id.signInPasswordError);
        credential = findViewById(R.id.signInCredentials);
        password = findViewById(R.id.signInPassword);
        signIn = findViewById(R.id.signIn);
        signUp = findViewById(R.id.signInCreateAccount);

        credential.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RegEx.isCredentialValid(credential, credentialError, SignIn.this)) {
                    passwordError.setEnabled(true);
                }

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
                if (RegEx.isPasswordValid(password, passwordError, SignIn.this)) {
                    signIn.setEnabled(true);
                }
            }
        });

        signIn.setOnClickListener(v -> {
            if (!credentialError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                Auth.signInForm(Objects.requireNonNull(credential.getText()).toString().trim(),
                        Objects.requireNonNull(password.getText()).toString().trim(), SignIn.this,
                        new Auth.OnResultListener() {
                            @Override
                            public void onResult(boolean result) {
                                if (result){
                                    startActivity(new Intent(SignIn.this, Main.class));
                                    finish();
                                }else
                                    Toast.makeText(SignIn.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, SignUp.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}