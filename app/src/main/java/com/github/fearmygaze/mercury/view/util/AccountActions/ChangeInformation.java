package com.github.fearmygaze.mercury.view.util.AccountActions;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangeInformation extends AppCompatActivity {

    TextInputLayout verifyPasswordError;
    TextInputEditText verifyPassword;

    MaterialButton cancel, next;

    String changeType, senderID;

    String userEmail;
    Bundle bundle;
    User user;
    FirebaseUser fireUser;
    Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User.PARCEL);
        changeType = bundle.getString("type");
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || changeType == null || fireUser == null)
            onBackPressed();

        userEmail = fireUser.getEmail();
        auth = new Auth(ChangeInformation.this);

        verifyPasswordError = findViewById(R.id.changeInformationVerifyPasswordError);
        verifyPassword = findViewById(R.id.changeInformationVerifyPassword);

        cancel = findViewById(R.id.changeInformationStepCancel);
        next = findViewById(R.id.changeInformationStepNext);

        cancel.setOnClickListener(v -> onBackPressed());

        verifyPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                next.setEnabled(RegEx.isPasswordValid(verifyPassword, verifyPasswordError, ChangeInformation.this));
            }
        });

        next.setOnClickListener(v -> {
            auth.signOut(false);
            String password = Objects.requireNonNull(verifyPassword.getText()).toString().trim();
            auth.signIn(userEmail, password, new CallBackResponse<String>() {
                @Override
                public void onSuccess(String object) {
                    switch (changeType) {
                        case "email":
                            startActivity(new Intent(ChangeInformation.this, ChangeEmail.class)
                                    .putExtra(User.PARCEL, user)
                                    .putExtra("email", userEmail));
                            finish();
                            break;
                        case "password":
                            startActivity(new Intent(ChangeInformation.this, ChangePassword.class));
                            finish();
                            break;
                        case "delete":
                            startActivity(new Intent(ChangeInformation.this, DeleteAccount.class)
                                    .putExtra(User.PARCEL, user));
                            finish();
                            break;
                    }
                }

                @Override
                public void onError(String message) {
                    this.onFailure(message);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ChangeInformation.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
