package com.github.fearmygaze.mercury.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.UserController;
import com.github.fearmygaze.mercury.interfaces.IFormSignUp;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.TextHandler;
import com.github.fearmygaze.mercury.view.activity.Starting;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class SignUp extends Fragment {
    View view;

    ShapeableImageView signUpImage;
    TextView signUpAddImage;

    TextInputEditText signUpUsername, signUpEmail, signUpPassword, signUpConfirmPassword;
    TextInputLayout signUpUsernameError, signUpEmailError, signUpPasswordError, signUpConfirmPasswordError;

    MaterialCheckBox signUpAgreeToTerms;
    TextView signUpTerms;

    MaterialCheckBox signUpAgreeToAdultAge;
    TextView signUpAdultAge;

    MaterialButton signUpButton;

    TextView signUpToSignIn;

    String base64Image;

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

        signUpButton.setOnClickListener(v -> checkForErrorsAndRegister());

        signUpAgreeToTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            signUpAgreeToAdultAge.setEnabled(isChecked);
            if (!isChecked) {
                signUpAgreeToAdultAge.setChecked(false);
            }
        });

        signUpAgreeToAdultAge.setOnCheckedChangeListener((buttonView, isChecked) -> signUpButton.setEnabled(isChecked && signUpAgreeToTerms.isChecked()));

        signUpUsername.addTextChangedListener(new TextHandler(signUpUsernameError));
        signUpEmail.addTextChangedListener(new TextHandler(signUpEmailError));
        signUpPassword.addTextChangedListener(new TextHandler(signUpPasswordError));
        signUpConfirmPassword.addTextChangedListener(new TextHandler(signUpConfirmPasswordError));

        signUpImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            pickImage.launch(intent);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).reInitiateFragmentSignIn());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    try {

                        Bitmap output = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri), 1024, 1024, true);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        output.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        base64Image = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                        signUpAddImage.setVisibility(View.GONE);
                        signUpImage.setImageURI(uri);
                        stateOfCells(true);

                    } catch (IOException e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void stateOfCells(Boolean state) { //TODO: ADD GROUPING
        signUpUsername.setEnabled(state);
        signUpEmail.setEnabled(state);
        signUpPassword.setEnabled(state);
        signUpConfirmPassword.setEnabled(state);
        signUpAgreeToTerms.setEnabled(state);
        signUpTerms.setEnabled(state);
        signUpAgreeToAdultAge.setEnabled(state);
        signUpAdultAge.setEnabled(state);
    }

    private void openTermsDialog() {
        Toast.makeText(requireContext(), "Open Dialog", Toast.LENGTH_SHORT).show();
    }

    private void openAdultAgeDialog() {
        Toast.makeText(requireContext(), "Open Dialog", Toast.LENGTH_SHORT).show();
    }

    private void checkForErrorsAndRegister() {//TODO: We need to add minimum and maximum characters on the Form
        if (TextHandler.isTextInputLengthCorrect(signUpUsername, signUpUsernameError, 50, getContext()) &&
                TextHandler.isTextInputLengthCorrect(signUpEmail, signUpEmailError, 50, getContext()) && !base64Image.isEmpty()) {
            if (RegEx.isSignUpFormValid(signUpUsername, signUpUsernameError, signUpPassword, signUpPasswordError, signUpConfirmPassword, signUpConfirmPasswordError, 300, getContext())) {
                String username = Objects.requireNonNull(signUpUsername.getText()).toString().trim();
                String email = Objects.requireNonNull(signUpEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(signUpPassword.getText()).toString().trim();

                UserController.signUp(username, email, password, base64Image, requireContext(), new IFormSignUp() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        ((Starting) requireActivity()).replaceFragment(((Starting) requireActivity()).reInitiateFragmentSignIn());
                    }

                    @Override
                    public void onValidationError(String message) {
                        //TODO: ADD here the validation errors (Maybe with a "custom" textHandler to add the errors under the correct box)
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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