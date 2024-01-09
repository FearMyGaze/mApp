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
import androidx.appcompat.content.res.AppCompatResources;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.UIAction;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.SignCallBackResponse;
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignIn extends AppCompatActivity {

    MaterialToolbar toolbar;
    TextInputLayout emailError, passwordError;
    TextInputEditText email, password;
    MaterialButton forgotPassword, signIn;

    UserActions userActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        toolbar = findViewById(R.id.signInToolBar);
        emailError = findViewById(R.id.signInEmailError);
        email = findViewById(R.id.signInEmail);
        passwordError = findViewById(R.id.signInPasswordError);
        password = findViewById(R.id.signInPassword);
        forgotPassword = findViewById(R.id.signInForgotPassword);
        signIn = findViewById(R.id.signInBtn);

        userActions = new UserActions(SignIn.this);
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
            userActions.passwordReset(email.getText().toString().trim(), new CallBackResponse<String>() {
                @Override
                public void onSuccess(String object) {
                    Toast.makeText(SignIn.this, getString(R.string.signInForgot), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    this.onFailure(message);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        signIn.setOnClickListener(v -> {
            if (!emailError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                UIAction.closeKeyboard(SignIn.this);
                View dialogView = LayoutInflater.from(SignIn.this).inflate(R.layout.dialog_sign_up, null);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignIn.this);
                builder.setCancelable(true)
                        .setView(dialogView)
                        .setBackground(AppCompatResources.getDrawable(SignIn.this, R.color.basicBackground))
                        .setTitle(getString(R.string.signInDialogTitle))
                        .setMessage(getString(R.string.signInDialogMessage));
                AlertDialog dialog = builder.show();
                userActions.signIn(email.getText().toString().trim(),
                        password.getText().toString().trim(),
                        new SignCallBackResponse<String>() {
                            @Override
                            public void onSuccess(String message) {
                                dialog.dismiss();
                                Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(SignIn.this, Main.class));
                            }

                            @Override
                            public void onError(int code, String message) {
                                dialog.dismiss();
                                if (code == 1) {
                                    Snackbar.make(signIn, getString(R.string.signInResend), 9000)
                                            .setAction(getString(R.string.generalSend), view ->
                                                    userActions.verificationEmail(new CallBackResponse<String>() {
                                                        @Override
                                                        public void onSuccess(String message) {
                                                            Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onError(String message) {
                                                            Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onFailure(String message) {
                                                            Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }))
                                            .show();
                                } else {
                                    Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                dialog.dismiss();
                                Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignIn.this, Welcome.class));
        finish();
    }
}
