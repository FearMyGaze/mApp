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

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.view.activity.Main;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignIn extends Fragment {

    TextInputLayout signInEmailAddressError, signInPasswordError;
    TextInputEditText signInEmailAddress, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    FirebaseAuth auth;

    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        auth = FirebaseAuth.getInstance();

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
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(
                Objects.requireNonNull(signInEmailAddress.getText()).toString(),
                Objects.requireNonNull(signInPassword.getText()).toString())
                .addOnFailureListener(e -> showToast(e.getMessage(),1))
                .addOnSuccessListener(authResult -> {
                    startActivity(new Intent(requireActivity(), Main.class));
                    requireActivity().finish();
                });
    }

    private void showToast(String message, int length){
        Toast.makeText(getContext(), message, length).show();
    }


}