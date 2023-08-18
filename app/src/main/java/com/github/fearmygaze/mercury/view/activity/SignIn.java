package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    TextInputLayout emailError, passwordError;
    TextInputEditText email, password;
    MaterialButton forgotPassword, signIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailError = findViewById(R.id.signInEmailError);
        email = findViewById(R.id.signInEmail);
        passwordError = findViewById(R.id.signInPasswordError);
        password = findViewById(R.id.signInPassword);
        forgotPassword = findViewById(R.id.signInForgotPassword);
        signIn = findViewById(R.id.signIn);
        signUp = findViewById(R.id.signInCreateAccount);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordError.setEnabled(RegEx.isEmailValid(email, emailError, SignIn.this));
                forgotPassword.setEnabled(RegEx.isEmailValid(email, emailError, SignIn.this));
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
                signIn.setEnabled(RegEx.isPasswordValid(password, passwordError, SignIn.this));
            }
        });

        forgotPassword.setOnClickListener(v -> {
            Auth.sendPasswordResetEmail(Objects.requireNonNull(email.getText()).toString().trim(), SignIn.this, new OnResponseListener() {
                @Override
                public void onSuccess(int code) {
                    if (code == 0) {
                        Toast.makeText(SignIn.this, "Email has been send", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        signIn.setOnClickListener(v -> {
            if (!emailError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                Tools.closeKeyboard(SignIn.this);
                Auth.signIn(Objects.requireNonNull(email.getText()).toString().trim(),
                        Objects.requireNonNull(password.getText()).toString().trim(),
                        SignIn.this, new OnDataResponseListener() {
                            @Override
                            public void onSuccess(int code, Object data) {
                                if (code == 0) {
                                    Tools.createSettingsPreference(SignIn.this);
                                    Tools.writeStrPreference("current", data.toString(), SignIn.this);
                                    finish();
                                    startActivity(new Intent(SignIn.this, Main.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else if (code == 1) {
                                    Snackbar.make(signIn, "Please activate your account, or press 'send' to send a new verification email", 9000)
                                            .setAction("Send", view ->
                                                    Auth.sendVerificationEmail(FirebaseAuth.getInstance().getCurrentUser(), SignIn.this,
                                                            new OnResponseListener() {
                                                                @Override
                                                                public void onSuccess(int code1) {
                                                                    if (code1 == 0)
                                                                        Toast.makeText(SignIn.this, "New Verification email send", Toast.LENGTH_SHORT).show();
                                                                    else
                                                                        Toast.makeText(SignIn.this, "Error", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onFailure(String message) {
                                                                    Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                    )
                                            ).show();
                                } else {
                                    Toast.makeText(SignIn.this, "Error", Toast.LENGTH_SHORT).show();
                                }
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
