package com.github.fearmygaze.mercury.view.util.AccountActions;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.view.activity.Main;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ChangeEmail extends AppCompatActivity {

    TextView currentEmail;
    TextInputLayout emailError;
    TextInputEditText email;

    MaterialButton cancel, next;

    Bundle bundle;
    User user;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        currentEmail = findViewById(R.id.changeEmailCurrent);
        emailError = findViewById(R.id.changeEmailError);
        email = findViewById(R.id.changeEmail);
        cancel = findViewById(R.id.changeEmailCancel);
        next = findViewById(R.id.changeEmailNext);

        bundle = getIntent().getExtras();
        if (bundle == null) finish();
        user = bundle.getParcelable(User.PARCEL);
        userEmail = getIntent().getStringExtra("email");
        if (user == null || userEmail == null) finish();

        currentEmail.setText(userEmail);
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

        next.setOnClickListener(v -> {
            String updatedEmail = Objects.requireNonNull(email.getText()).toString().trim();
            new Auth(v.getContext()).changeEmail(updatedEmail, new CallBackResponse<String>() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(ChangeEmail.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChangeEmail.this, Main.class));
                    finish();
                }

                @Override
                public void onError(String message) {
                    this.onFailure(message);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ChangeEmail.this, message, Toast.LENGTH_SHORT).show();
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
