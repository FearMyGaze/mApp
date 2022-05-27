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

import java.util.Objects;

public class SignIn extends Fragment {

    TextInputLayout signInWithCredentialsError, signInPasswordError;
    TextInputEditText signInWithCredentials, signInPassword;

    TextView signInCreateNewAccount;

    MaterialButton signInButton;

    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInWithCredentials = view.findViewById(R.id.signInWithCredentials);
        signInWithCredentialsError = view.findViewById(R.id.signInWithCredentialsError);

        signInPassword = view.findViewById(R.id.signInPassword);
        signInPasswordError = view.findViewById(R.id.signInPasswordError);

        signInCreateNewAccount = view.findViewById(R.id.signInCreateNewAccount);

        signInButton = view.findViewById(R.id.signInButton);

        /*
         * The moment the TextInputEditText is filled with a text after an error occurred the error
         *   vanishes from the text that was changed
         * */
        signInWithCredentials.addTextChangedListener(new TextHandler(signInWithCredentialsError));
        signInPassword.addTextChangedListener(new TextHandler(signInPasswordError));

        signInButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), Main.class));
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        signInCreateNewAccount.setOnClickListener(view1 -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signUp));


        return view;
    }

    private void userCreate(){
        TextHandler.isTextInputEmpty(signInWithCredentials,signInWithCredentialsError, getContext());
        TextHandler.isTextInputEmpty(signInPassword,signInPasswordError, getContext());

        if (!signInWithCredentialsError.isErrorEnabled() || !signInPasswordError.isErrorEnabled()){
            if (RegEx.isPasswordValid(Objects.requireNonNull(signInPassword.getText()).toString(),signInPasswordError,getContext())){
                showToast("User logged in",0); //TODO: User sign In
            }
        }
    }


    private void showToast(String message, int length){
        Toast.makeText(getContext(), message, length).show();
    }


}