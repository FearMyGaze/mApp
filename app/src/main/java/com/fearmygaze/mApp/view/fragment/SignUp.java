package com.fearmygaze.mApp.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class SignUp extends Fragment {

    ShapeableImageView signUpImage;
    TextView signUpAddImage;

    TextInputEditText signUpUsername, signUpEmail, signUpName, signUpPassword, signUpConfirmPassword;
    TextInputLayout signUpUsernameError, signUpEmailError, signUpNameError, signUpPasswordError, signUpConfirmPasswordError;

    CheckBox signUpAgreeToTerms;
    TextView signUpTerms;

    MaterialButton signUpButton;

    TextView signUpToSignIn;

    String stringConvertedImage;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        signUpImage = view.findViewById(R.id.signUpImage);
        signUpAddImage = view.findViewById(R.id.signUpAddImage);

        signUpUsername = view.findViewById(R.id.signUpUsername);
        signUpUsernameError = view.findViewById(R.id.signUpUsernameError);

        signUpName = view.findViewById(R.id.signUpName);
        signUpNameError = view.findViewById(R.id.signUpNameError);

        signUpEmail = view.findViewById(R.id.signUpEmailAddress);
        signUpEmailError = view.findViewById(R.id.signUpEmailAddressError);

        /*
        * TODO: DatePicker stuff
        * */

        signUpPassword = view.findViewById(R.id.signUpPassword);
        signUpPasswordError = view.findViewById(R.id.signUpPasswordError);

        signUpConfirmPassword = view.findViewById(R.id.signUpConfirmPassword);
        signUpConfirmPasswordError = view.findViewById(R.id.signUpConfirmPasswordError);

        signUpAgreeToTerms = view.findViewById(R.id.signUpAgreeToTerms);
        signUpTerms = view.findViewById(R.id.signUpTerms);

        signUpButton = view.findViewById(R.id.signUpButton);

        signUpToSignIn = view.findViewById(R.id.signUpToSignIn);

        stateOfCells(false);

        signUpTerms.setOnClickListener(view1 -> openTermsDialog());

        signUpToSignIn.setOnClickListener(view1 -> ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn));

        signUpButton.setOnClickListener(view1 -> checkForErrors());

        signUpAgreeToTerms.setOnCheckedChangeListener((compoundButton, b) -> signUpButton.setEnabled(b));

        /*
         * The moment the TextInputEditText is filled with a text after an error occurred the error
         *   vanishes from the text that was changed
         * */
        signUpUsername.addTextChangedListener(new TextHandler(signUpUsernameError));
        signUpEmail.addTextChangedListener(new TextHandler(signUpEmailError));
        signUpName.addTextChangedListener(new TextHandler(signUpNameError));
        signUpPassword.addTextChangedListener(new TextHandler(signUpPasswordError));
        signUpConfirmPassword.addTextChangedListener(new TextHandler(signUpConfirmPasswordError));

        signUpImage.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).signIn);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        signUpAgreeToTerms.setChecked(false);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),uri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);

                    stringConvertedImage = null;
                    stringConvertedImage = stream.toString();

                    signUpAddImage.setVisibility(View.GONE);
                    signUpImage.setImageBitmap(bitmap);

                    stateOfCells(true);
                } catch (IOException e) {
                   showToast(e.getMessage(),1);
                }
            }
        }
    );

    private void stateOfCells(Boolean state){
        signUpEmail.setEnabled(state);
        signUpName.setEnabled(state);
        signUpPassword.setEnabled(state);
        signUpConfirmPassword.setEnabled(state);
        signUpAgreeToTerms.setEnabled(state);
        signUpTerms.setEnabled(state);
    }

    private void openTermsDialog(){
        showToast("Open Dialog",0);
    }

    private void checkForErrors(){
        TextHandler.isTextInputEmpty(signUpUsername,signUpUsernameError,requireActivity());
        TextHandler.isTextInputEmpty(signUpEmail, signUpEmailError,requireActivity());
        TextHandler.isTextInputEmpty(signUpName,signUpNameError,requireActivity());
        TextHandler.isTextInputEmpty(signUpPassword,signUpPasswordError,requireActivity());
        TextHandler.isTextInputEmpty(signUpConfirmPassword,signUpConfirmPasswordError,requireActivity());

        if (!signUpUsernameError.isErrorEnabled() || !signUpEmailError.isErrorEnabled() || !signUpNameError.isErrorEnabled()
                || !signUpPasswordError.isErrorEnabled() || !signUpConfirmPasswordError.isErrorEnabled() || !stringConvertedImage.isEmpty() ){
            if (TextHandler.IsTextInputsEqual(signUpPassword, signUpConfirmPassword, signUpPasswordError, requireActivity())) {
                if (RegEx.isPasswordValid(Objects.requireNonNull(signUpPassword.getText()).toString(), signUpPasswordError, getContext())){
                    showToast("True",0); //TODO: Create user
                }
            }
        }
    }
    private void showToast(String message, int length){
        Toast.makeText(getContext(), message, length).show();
    }
}