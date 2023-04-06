package com.github.fearmygaze.mercury.view.util;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ChangeInformation extends AppCompatActivity {

    TextInputLayout verifyPasswordError;
    TextInputEditText verifyPassword;

    MaterialButton cancel, next;

    Intent intent;
    String changeType, senderID;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        intent = getIntent();
        senderID = intent.getStringExtra("userID");
        changeType = intent.getStringExtra("type");

        user = AppDatabase.getInstance(ChangeInformation.this).userDao().getUserByUserUID(senderID);

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

        next.setOnClickListener(v ->
                Auth.authenticate(user.email, Objects.requireNonNull(verifyPassword.getText()).toString().trim(), ChangeInformation.this, new Auth.OnResponseListener() {
                    @Override
                    public void onResult(int resultCode) {
                        if (resultCode == 1) {
                            if (changeType.equals("email")) {
                                startActivity(new Intent(ChangeInformation.this, ChangeEmail.class)
                                        .putExtra("userID", user.userUID));
                                finish();
                            } else {
                                startActivity(new Intent(ChangeInformation.this, ChangePassword.class)
                                        .putExtra("userID", user.userUID));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ChangeInformation.this, message, Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}