package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.util.RegEx;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignIn extends Fragment {

    TextInputLayout signInEmailError, signInPasswordError;
    TextInputEditText signInEmail, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInEmail = view.findViewById(R.id.signInEmail);
        signInEmailError = view.findViewById(R.id.signInWithCredentialsError);

        signInPassword = view.findViewById(R.id.signInPassword);
        signInPasswordError = view.findViewById(R.id.signInPasswordError);

        signInCreateNewAccount = view.findViewById(R.id.signInCreateNewAccount);

        signInButton = view.findViewById(R.id.signInButton);

        /*
         * The moment the TextInputEditText is filled with a text after an error occurred the error
         *   vanishes from the text that was changed
         * */
        signInEmail.addTextChangedListener(new TextHandler(signInEmailError));
        signInPassword.addTextChangedListener(new TextHandler(signInPasswordError));

        signInButton.setOnClickListener(v -> userLogin());

        signInCreateNewAccount.setOnClickListener(v -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).reInitiateFragmentSignUp()));

        return view;
    }

    private void userLogin(){
        TextHandler.isTextInputEmpty(signInEmail, signInEmailError, getContext());
        TextHandler.isTextInputEmpty(signInPassword,signInPasswordError, getContext());

        if (!signInEmailError.isErrorEnabled() || !signInPasswordError.isErrorEnabled()){
            if (RegEx.isPasswordValid(Objects.requireNonNull(signInPassword.getText()).toString(),signInPasswordError,getContext())){

            }
        }
    }

}