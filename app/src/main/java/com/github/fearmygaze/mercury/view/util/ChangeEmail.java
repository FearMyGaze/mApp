package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
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

public class ChangeEmail extends AppCompatActivity {

    TextView currentEmail;
    TextInputLayout emailError;
    TextInputEditText email;

    MaterialButton cancel, next;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        currentEmail = findViewById(R.id.changeEmailCurrent);
        emailError = findViewById(R.id.changeEmailError);
        email = findViewById(R.id.changeEmail);
        cancel = findViewById(R.id.changeEmailCancel);
        next = findViewById(R.id.changeEmailNext);

        user = AppDatabase.getInstance(ChangeEmail.this).userDao().getUserByUserUID(getIntent().getStringExtra("userID"));
        currentEmail.setText(user.email);

        cancel.setOnClickListener(v -> onBackPressed());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                next.setEnabled(RegEx.isEmailValid(email, emailError, ChangeEmail.this));
            }
        });

        next.setOnClickListener(v ->
                Auth.updateEmail(Objects.requireNonNull(email.getText()).toString().trim(), user, ChangeEmail.this, new Auth.OnResponseListener() {
                    @Override
                    public void onResult(int resultCode) {
                        if (resultCode == 1) {
                            Toast.makeText(ChangeEmail.this, "Success", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else Toast.makeText(ChangeEmail.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ChangeEmail.this, message, Toast.LENGTH_SHORT).show();
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