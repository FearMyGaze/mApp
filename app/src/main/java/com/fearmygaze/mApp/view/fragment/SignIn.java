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
import com.fearmygaze.mApp.util.RegEx;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.activity.Main;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

public class SignIn extends Fragment {

    TextInputLayout signInEmailError, signInPasswordError;
    TextInputEditText signInEmail, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode(Locale.getDefault().getLanguage());
        user = firebaseAuth.getCurrentUser();


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
                firebaseAuth //TODO: Add email Verification
                        .signInWithEmailAndPassword(Objects.requireNonNull(signInEmail.getText()).toString(), signInPassword.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            startActivity(new Intent(getActivity(), Main.class));
                            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            requireActivity().finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

}