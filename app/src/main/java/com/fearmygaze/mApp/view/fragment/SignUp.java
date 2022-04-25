package com.fearmygaze.mApp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.activity.Main;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignUp extends Fragment {

    ShapeableImageView signUpImage;
    TextView signUpAddImage;

    TextInputEditText signUpEmail, signUpName, signUpPassword, signUpConfirmPassword;
    TextInputLayout signUpEmailError, signUpNameError, signUpPasswordError, signUpConfirmPasswordError;

    CheckBox signUpAgreeToTerms;
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

        signUpEmail = view.findViewById(R.id.signUpEmailAddress);
        signUpEmailError = view.findViewById(R.id.signUpEmailAddressError);

        signUpName = view.findViewById(R.id.signUpName);
        signUpNameError = view.findViewById(R.id.signUpNameError);

        signUpPassword = view.findViewById(R.id.signUpPassword);
        signUpPasswordError = view.findViewById(R.id.signUpPasswordError);

        signUpConfirmPassword = view.findViewById(R.id.signUpConfirmPassword);
        signUpConfirmPasswordError = view.findViewById(R.id.signUpConfirmPasswordError);

        signUpAgreeToTerms = view.findViewById(R.id.signUpAgreeToTerms);
        signUpTerms = view.findViewById(R.id.signUpTerms);

        signUpButton = view.findViewById(R.id.signUpButton);

        signUpToSignIn = view.findViewById(R.id.signUpToSignIn);

        signUpAddImage.setOnClickListener(view1 -> showToast("add image",1));

        signUpTerms.setOnClickListener(view1 -> showToast("Open Dialog", 0));

        signUpToSignIn.setOnClickListener(view1 -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn));

        signUpButton.setOnClickListener(view1 -> checkForErrors());

        signUpAgreeToTerms.setOnCheckedChangeListener((compoundButton, b) -> signUpButton.setEnabled(b));

        /*
         * The moment the TextInputEditText is filled with a text after an error occurred the error
         *   vanishes from the text that was changed
         * */
        signUpEmail.addTextChangedListener(new TextHandler(signUpEmailError));
        signUpName.addTextChangedListener(new TextHandler(signUpNameError));
        signUpPassword.addTextChangedListener(new TextHandler(signUpPasswordError));
        signUpConfirmPassword.addTextChangedListener(new TextHandler(signUpConfirmPasswordError));


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    private void checkForErrors(){
        //TODO: Check for empty image
        TextHandler.isTextInputEmpty(signUpEmail, signUpEmailError,requireActivity());
        TextHandler.isTextInputEmpty(signUpName,signUpNameError,requireActivity());
        TextHandler.isTextInputEmpty(signUpPassword,signUpPasswordError,requireActivity());
        TextHandler.isTextInputEmpty(signUpConfirmPassword,signUpConfirmPasswordError,requireActivity());

        if (!signUpEmailError.isErrorEnabled() || !signUpNameError.isErrorEnabled()
                || !signUpPasswordError.isErrorEnabled() || !signUpConfirmPasswordError.isErrorEnabled()){
            if (TextHandler.IsTextInputsEqual(signUpPassword,signUpConfirmPassword,signUpPasswordError,requireActivity())){
                verifyPassword();
                showToast("inside if",1);
            }
        }

    }

    private void verifyPassword(){ //TODO: Add a Simple RegEx for password (email not in need because we are gonna send a verification email
        signUp(); //if succeed then
    }

    private void signUp() {
        String email = Objects.requireNonNull(signUpEmail.getText()).toString();
        String name = Objects.requireNonNull(signUpName.getText()).toString();
        String password = Objects.requireNonNull(signUpPassword.getText()).toString();

        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification()
                                .addOnSuccessListener(unused -> {
                                    showToast(getResources().getString(R.string.signUpEmailVerification) + user.getEmail(), 1);
                                    ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn);
                                })
                                .addOnFailureListener(e -> showToast(e.getMessage(), 1));
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                //.setPhotoUri(image)
                                .setDisplayName(name)
                                .build();
                        user.updateProfile(profileChangeRequest);
                    }
                })
                .addOnFailureListener(e -> showToast(e.getMessage(),1));
    }

    private void showToast(String message, int length){
        Toast.makeText(getContext(), message, length).show();
    }
}