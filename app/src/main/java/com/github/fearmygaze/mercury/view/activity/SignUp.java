package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    ShapeableImageView goBack, userImage;
    TextInputLayout displayNameError, usernameError, emailError, passwordError;
    TextInputEditText displayName, username, email, password;
    MaterialButton chooseImage, createAccount;

    Uri imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        goBack = findViewById(R.id.signupGoBack);
        displayNameError = findViewById(R.id.signUpDisplayNameError);
        displayName = findViewById(R.id.signUpDisplayName);
        usernameError = findViewById(R.id.signUpUsernameError);
        username = findViewById(R.id.signUpUsername);
        emailError = findViewById(R.id.signUpEmailError);
        email = findViewById(R.id.signUpEmail);
        passwordError = findViewById(R.id.signUpPasswordError);
        password = findViewById(R.id.signUpPassword);
        userImage = findViewById(R.id.signUpUserImage);
        chooseImage = findViewById(R.id.signUpChooseImage);
        createAccount = findViewById(R.id.signUpCreateAccount);

        goBack.setOnClickListener(v -> onBackPressed());

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
                displayNameError.setEnabled(RegEx.isUsernameValid(username, usernameError, SignUp.this));
            }
        });

        displayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordError.setEnabled(RegEx.isNameValid(displayName, displayNameError, SignUp.this));
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
                chooseImage.setEnabled(RegEx.isPasswordValid(password, passwordError, SignUp.this));
            }
        });

        chooseImage.setOnClickListener(v -> pickImage.launch(Tools.imageSelector()));

        createAccount.setOnClickListener(v -> {
            String sEmail = Objects.requireNonNull(email.getText()).toString().trim();
            String sUsername = Objects.requireNonNull(username.getText()).toString().trim();
            String sDisplayName = Objects.requireNonNull(displayName.getText()).toString().trim();
            String sPassword = Objects.requireNonNull(password.getText()).toString().trim();
            if (!emailError.isErrorEnabled() && !usernameError.isErrorEnabled() && !displayNameError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                Auth.signUpForm(sEmail, emailError, sUsername, usernameError, sDisplayName, sPassword, imageData, SignUp.this, new Auth.OnResponseListener() {
                    @Override
                    public void onResult(int resultCode) {
                        if (resultCode == 1) {
                            startActivity(new Intent(SignUp.this, SignIn.class));
                            finish();
                        } else {
                            Toast.makeText(SignUp.this, getString(R.string.blameError), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUp.this, SignIn.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case RESULT_OK:
                        if (result.getData() != null) {
                            imageData = result.getData().getData();
                            Glide.with(userImage).load(imageData).centerCrop().apply(new RequestOptions().override(1024)).into(userImage);
                            userImage.setImageURI(imageData);
                            createAccount.setEnabled(true);
                        }
                        break;
                    case RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(SignUp.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
    );
}