package com.fearmygaze.mApp.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.util.RegEx;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Objects;

public class SignUp extends Fragment {

    ShapeableImageView signUpImage;
    TextView signUpAddImage;

    TextInputEditText signUpUsername, signUpEmail, signUpPassword, signUpConfirmPassword;
    TextInputLayout signUpUsernameError, signUpEmailError, signUpPasswordError, signUpConfirmPasswordError;

    CheckBox signUpAgreeToTerms;
    TextView signUpTerms;

    CheckBox signUpAgreeToAdultAge;
    TextView signUpAdultAge;

    MaterialButton signUpButton;

    TextView signUpToSignIn;

    String stringConvertedImage;

    View view;

    Bitmap bitmap;
    Uri downloadLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        signUpImage = view.findViewById(R.id.signUpImage);
        signUpAddImage = view.findViewById(R.id.signUpAddImage);

        signUpUsername = view.findViewById(R.id.signUpUsername);
        signUpUsernameError = view.findViewById(R.id.signUpUsernameError);

        signUpEmail = view.findViewById(R.id.signUpEmailAddress);
        signUpEmailError = view.findViewById(R.id.signUpEmailAddressError);

        signUpPassword = view.findViewById(R.id.signUpPassword);
        signUpPasswordError = view.findViewById(R.id.signUpPasswordError);

        signUpConfirmPassword = view.findViewById(R.id.signUpConfirmPassword);
        signUpConfirmPasswordError = view.findViewById(R.id.signUpConfirmPasswordError);

        signUpAgreeToTerms = view.findViewById(R.id.signUpAgreeToTerms);
        signUpTerms = view.findViewById(R.id.signUpTerms);

        signUpAgreeToAdultAge = view.findViewById(R.id.signUpAgreeToAdultAge);
        signUpAdultAge = view.findViewById(R.id.signUpAdultAge);

        signUpButton = view.findViewById(R.id.signUpButton);

        signUpToSignIn = view.findViewById(R.id.signUpToSignIn);

        stateOfCells(false);

        signUpTerms.setOnClickListener(v -> openTermsDialog());

        signUpAdultAge.setOnClickListener(v -> openAdultAgeDialog());

        signUpToSignIn.setOnClickListener(v -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).reInitiateFragmentSignIn()));

        signUpButton.setOnClickListener(v -> checkForErrors());

        signUpAgreeToTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            signUpAgreeToAdultAge.setEnabled(isChecked);
            if (!isChecked){
                signUpAgreeToAdultAge.setChecked(false);
            }
        });

        signUpAgreeToAdultAge.setOnCheckedChangeListener((buttonView, isChecked) -> signUpButton.setEnabled(isChecked && signUpAgreeToTerms.isChecked()));

        /*
         * The moment the TextInputEditText is filled with a text after an error occurred the error
         *   vanishes from the text that was changed
         * */
        signUpUsername.addTextChangedListener(new TextHandler(signUpUsernameError));
        signUpEmail.addTextChangedListener(new TextHandler(signUpEmailError));
        signUpPassword.addTextChangedListener(new TextHandler(signUpPasswordError));
        signUpConfirmPassword.addTextChangedListener(new TextHandler(signUpConfirmPasswordError));

        signUpImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).reInitiateFragmentSignIn());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                Log.d("File",uri.toString());
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),uri);
                    Log.d("File",bitmap.toString());

                    signUpAddImage.setVisibility(View.GONE);
                    signUpImage.setImageURI(uri);
                    stateOfCells(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    );

    private void stateOfCells(Boolean state){
        signUpUsername.setEnabled(state);
        signUpEmail.setEnabled(state);
        signUpPassword.setEnabled(state);
        signUpConfirmPassword.setEnabled(state);
        signUpAgreeToTerms.setEnabled(state);
        signUpTerms.setEnabled(state);
        signUpAgreeToAdultAge.setEnabled(state);
        signUpAdultAge.setEnabled(state);
    }

    private void openTermsDialog(){
        Toast.makeText(getContext(), "Open Dialog", Toast.LENGTH_SHORT).show();
    }

    private void openAdultAgeDialog(){
        Toast.makeText(getContext(), "Open Dialog", Toast.LENGTH_SHORT).show();
    }


    private void checkForErrors(){
        TextHandler.isTextInputEmpty(signUpUsername,signUpUsernameError,requireActivity());
        TextHandler.isTextInputEmpty(signUpEmail, signUpEmailError,requireActivity());
        TextHandler.isTextInputEmpty(signUpPassword,signUpPasswordError,requireActivity());
        TextHandler.isTextInputEmpty(signUpConfirmPassword,signUpConfirmPasswordError,requireActivity());

        if (!signUpUsernameError.isErrorEnabled() || !signUpEmailError.isErrorEnabled() || !signUpPasswordError.isErrorEnabled()
                || !signUpConfirmPasswordError.isErrorEnabled() || !stringConvertedImage.isEmpty()) {
            if (TextHandler.IsTextInputsEqual(signUpPassword, signUpConfirmPassword, signUpPasswordError, requireActivity())) {
                if (RegEx.isPasswordValid(Objects.requireNonNull(signUpPassword.getText()).toString(), signUpPasswordError, getContext())){

                }
            }
        }
    }


}