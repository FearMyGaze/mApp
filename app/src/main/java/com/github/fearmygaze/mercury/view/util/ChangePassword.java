package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.AuthEvents;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    TextInputLayout passwordError;
    TextInputEditText password;

    MaterialButton forgot, cancel, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passowrd);

        passwordError = findViewById(R.id.changePasswordError);
        password = findViewById(R.id.changePassword);
        forgot = findViewById(R.id.changePasswordForgot);
        cancel = findViewById(R.id.changePasswordCancel);
        next = findViewById(R.id.changePasswordNext);

        cancel.setOnClickListener(v -> onBackPressed());
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                next.setEnabled(RegEx.isPasswordValid(password, passwordError, ChangePassword.this));
            }
        });

        next.setOnClickListener(v -> {
            String updatedPassword = Objects.requireNonNull(password.getText()).toString().trim();
            AuthEvents.updatePassword(updatedPassword, ChangePassword.this, new OnResponseListener() {
                @Override
                public void onSuccess(int code) {
                    if (code == 0) {
                        Toast.makeText(ChangePassword.this, "Success", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else
                        Toast.makeText(ChangePassword.this, "Error", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ChangePassword.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
