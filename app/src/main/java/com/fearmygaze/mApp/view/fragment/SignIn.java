package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignIn extends Fragment {

    TextInputLayout signInEmailAddressError, signInPasswordError;
    TextInputEditText signInEmailAddress, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInEmailAddress = view.findViewById(R.id.signInEmailAddress);
        signInEmailAddressError = view.findViewById(R.id.signInEmailAddressError);

        signInPassword = view.findViewById(R.id.signInPassword);
        signInPasswordError = view.findViewById(R.id.signInPasswordError);

        signInCreateNewAccount = view.findViewById(R.id.signInCreateNewAccount);

        signInButton = view.findViewById(R.id.signInButton);

        signInButton.setOnClickListener(view1 -> signIn());

        signInCreateNewAccount.setOnClickListener(view1 -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signUp));


        return view;
    }

    private void signIn() {
        showToast("Sign In",1);
    }

    private void showToast(String message, int length){
        Toast.makeText(getContext(), message, length).show();
    }


}