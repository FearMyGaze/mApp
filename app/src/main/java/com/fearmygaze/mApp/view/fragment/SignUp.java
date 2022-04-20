package com.fearmygaze.mApp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.view.activity.Main;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends Fragment {

    ShapeableImageView signUpImage;
    TextView signUpAddImage;

    TextInputEditText signUpEmailAddress, signUpName, signUpPassword, signUpConfirmPassword;
    TextInputLayout signUpEmailAddressError, signUpNameError, signUpPasswordError, signUpConfirmPasswordError;

    TextView signUpTerms;

    MaterialButton signUpButton;

    TextView signUpToSignIn;

    FirebaseUser user;
    FirebaseAuth auth;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        signUpImage = view.findViewById(R.id.signUpImage);
        signUpAddImage = view.findViewById(R.id.signUpAddImage);

        signUpEmailAddress = view.findViewById(R.id.signUpEmailAddress);
        signUpEmailAddressError = view.findViewById(R.id.signUpEmailAddressError);

        signUpName = view.findViewById(R.id.signUpName);
        signUpNameError = view.findViewById(R.id.signUpNameError);

        signUpPassword = view.findViewById(R.id.signUpPassword);
        signUpPasswordError = view.findViewById(R.id.signUpPasswordError);

        signUpConfirmPassword = view.findViewById(R.id.signUpConfirmPassword);
        signUpConfirmPasswordError = view.findViewById(R.id.signUpConfirmPasswordError);

        signUpTerms = view.findViewById(R.id.signUpTerms);

        signUpButton = view.findViewById(R.id.signUpButton);

        signUpToSignIn = view.findViewById(R.id.signUpToSignIn);

        signUpAddImage.setOnClickListener(view1 -> showToast("add image",1));

        signUpTerms.setOnClickListener(view1 -> showToast("Open Dialog", 0));

        signUpToSignIn.setOnClickListener(view1 -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn));

        signUpButton.setOnClickListener(view1 -> checkForErrors());




        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    private void signUp() {
        showToast("Just passing by: ->", 0);


        startActivity(new Intent(requireActivity(), Main.class));
        requireActivity().finish();
    }

    private void checkForErrors(){
        signUp(); //IF all true then go
    }

    private void showToast(String message, int length){
        Toast.makeText(getContext(), message, length).show();
    }
}