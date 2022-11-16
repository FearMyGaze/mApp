package com.github.fearmygaze.mercury.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.UserController;
import com.github.fearmygaze.mercury.interfaces.IFormSignIn;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.TextHandler;
import com.github.fearmygaze.mercury.view.activity.Main;
import com.github.fearmygaze.mercury.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignIn extends Fragment {
    View view;

    TextInputLayout signInCredentialsError, signInPasswordError;
    TextInputEditText signInCredentials, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInCredentials = view.findViewById(R.id.signInWithCredentials);
        signInCredentialsError = view.findViewById(R.id.signInWithCredentialsError);

        signInPassword = view.findViewById(R.id.signInPassword);
        signInPasswordError = view.findViewById(R.id.signInPasswordError);

        signInCreateNewAccount = view.findViewById(R.id.signInCreateNewAccount);

        signInButton = view.findViewById(R.id.signInButton);

        signInCredentials.addTextChangedListener(new TextHandler(signInCredentialsError));
        signInPassword.addTextChangedListener(new TextHandler(signInPasswordError));

        signInButton.setOnClickListener(v -> userLogin());

        signInCreateNewAccount.setOnClickListener(v -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).reInitiateFragmentSignUp()));

        return view;
    }

    private void userLogin() { //TODO: We need to add minimum and maximum characters on the Form
        if (TextHandler.isTextInputLengthCorrect(signInCredentials, signInCredentialsError, 50, getContext()) &&
                TextHandler.isTextInputLengthCorrect(signInPassword, signInPasswordError, 300, getContext())) {
            if (RegEx.isPasswordValid(Objects.requireNonNull(signInPassword.getText()).toString(), signInPasswordError, getContext())) {
                if (!signInCredentialsError.isErrorEnabled() && !signInPasswordError.isErrorEnabled()) {

                    String credential = Objects.requireNonNull(signInCredentials.getText()).toString();
                    String password = Objects.requireNonNull(signInPassword.getText()).toString();

                    UserController.signIn(credential, password, requireContext(), new IFormSignIn() {
                        @Override
                        public void onSuccess(int id, String message) {
                            startActivity(new Intent(requireActivity(), Main.class));
                            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            requireActivity().finish();
                        }

                        @Override
                        public void onValidationError(String message) {
                            //TODO: ADD here the validation errors (Maybe with a "custom" textHandler to add the errors under the correct box)
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}