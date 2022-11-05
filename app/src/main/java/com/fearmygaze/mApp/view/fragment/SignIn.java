package com.fearmygaze.mApp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fearmygaze.mApp.Controller.UserController;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.forms.IFormSignIn;
import com.fearmygaze.mApp.util.RegEx;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.activity.Main;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignIn extends Fragment {

    TextInputLayout signInCredentialsError, signInPasswordError;
    TextInputEditText signInCredentials, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                            Toast.makeText(getContext(),message, Toast.LENGTH_LONG).show();
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