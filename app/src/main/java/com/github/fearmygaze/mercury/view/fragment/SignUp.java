package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends Fragment {

    View view;
    ShapeableImageView goBack;
    TextInputLayout usernameError, emailError, passwordError;
    TextInputEditText username, email, password;
    MaterialButton createAccount;

    public SignUp() {

    }

    public static SignUp newInstance() {
        return new SignUp();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        goBack = view.findViewById(R.id.signupGoBack);
        usernameError = view.findViewById(R.id.signUpUsernameError);
        username = view.findViewById(R.id.signUpUsername);
        emailError = view.findViewById(R.id.signUpEmailError);
        email = view.findViewById(R.id.signUpEmail);
        passwordError = view.findViewById(R.id.signUpPasswordError);
        password = view.findViewById(R.id.signUpPassword);
        createAccount = view.findViewById(R.id.signUpCreateAccount);

        goBack.setOnClickListener(v -> ((Starting) requireActivity()).replaceFragment(SignIn.newInstance()));

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameError.setEnabled(RegEx.isEmailValid(email, emailError, view.getContext()));
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
                passwordError.setEnabled(RegEx.isUsernameValid(username, usernameError, view.getContext()));
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
                createAccount.setEnabled(RegEx.isPasswordValid(password, passwordError, view.getContext()));
            }
        });

        createAccount.setOnClickListener(v -> {
            String sEmail = Objects.requireNonNull(email.getText()).toString().trim();
            String sUsername = Objects.requireNonNull(username.getText()).toString().trim();
            String sPassword = Objects.requireNonNull(password.getText()).toString().trim();
            if (!emailError.isErrorEnabled() && !usernameError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.customAlertDialog);
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_sign_up, null);
                builder.setView(dialogView)
                        .setTitle(R.string.signUpDialogTitle)
                        .setMessage(R.string.signUpDialogMessage)
                        .setCancelable(false);
                AlertDialog dialog = builder.show();
                Auth.validateDataAndCreateUser(sUsername, sEmail, sPassword, view.getContext(), new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        dialog.dismiss();
                        switch (code) {
                            case 0:
                                Toast.makeText(view.getContext(), "We sent at your email a verification link, To continue please activate your account", Toast.LENGTH_SHORT).show();
                                ((Starting) requireActivity()).replaceFragment(SignIn.newInstance());
                                break;
                            case 1:
                                Tools.setErrorToLayout(emailError, getString(R.string.authEmail), true);
                                emailError.requestFocus();
                                break;
                            case 2:
                                Tools.setErrorToLayout(usernameError, getString(R.string.authUsername), true);
                                usernameError.requestFocus();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        dialog.dismiss();
                        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}
