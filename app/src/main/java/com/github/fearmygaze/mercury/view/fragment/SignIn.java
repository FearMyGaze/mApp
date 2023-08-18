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
import androidx.fragment.app.Fragment;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignIn extends Fragment {

    View view;
    TextInputLayout emailError, passwordError;
    TextInputEditText email, password;
    MaterialButton forgotPassword, signIn, signUp;

    public SignIn() {

    }

    public static SignIn newInstance() {
        return new SignIn();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        emailError = view.findViewById(R.id.signInEmailError);
        email = view.findViewById(R.id.signInEmail);
        passwordError = view.findViewById(R.id.signInPasswordError);
        password = view.findViewById(R.id.signInPassword);
        forgotPassword = view.findViewById(R.id.signInForgotPassword);
        signIn = view.findViewById(R.id.signIn);
        signUp = view.findViewById(R.id.signInCreateAccount);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordError.setEnabled(RegEx.isEmailValid(email, emailError, view.getContext()));
                forgotPassword.setEnabled(RegEx.isEmailValid(email, emailError, view.getContext()));
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
                signIn.setEnabled(RegEx.isPasswordValid(password, passwordError, view.getContext()));
            }
        });

        forgotPassword.setOnClickListener(v -> {
            Auth.sendPasswordResetEmail(Objects.requireNonNull(email.getText()).toString().trim(), view.getContext(), new OnResponseListener() {
                @Override
                public void onSuccess(int code) {
                    if (code == 0) {
                        Toast.makeText(view.getContext(), "Email has been send", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        signIn.setOnClickListener(v -> {
            if (!emailError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                Auth.signIn(Objects.requireNonNull(email.getText()).toString().trim(),
                        Objects.requireNonNull(password.getText()).toString().trim(),
                        view.getContext(), new OnResponseListener() {
                            @Override
                            public void onSuccess(int code) {
                                Tools.closeKeyboard(v.getContext());
                                if (code == 0) {
                                    Tools.createSettingsPreference(view.getContext());
                                    ((Starting) requireActivity()).replaceFragment(Loading.newInstance());
                                } else if (code == 1) {
                                    Snackbar.make(signIn, "Please activate your account, or press 'send' to send a new verification email", 9000)
                                            .setAction("Send", view ->
                                                    Auth.sendVerificationEmail(FirebaseAuth.getInstance().getCurrentUser(), view.getContext(),
                                                            new OnResponseListener() {
                                                                @Override
                                                                public void onSuccess(int code1) {
                                                                    if (code1 == 0)
                                                                        Toast.makeText(view.getContext(), "New Verification email send", Toast.LENGTH_SHORT).show();
                                                                    else
                                                                        Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onFailure(String message) {
                                                                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                    )
                                            ).show();
                                } else {
                                    Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signUp.setOnClickListener(v -> ((Starting) requireActivity()).replaceFragment(SignUp.newInstance()));

        return view;
    }
}
