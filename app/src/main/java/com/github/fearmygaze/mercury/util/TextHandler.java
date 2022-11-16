package com.github.fearmygaze.mercury.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.github.fearmygaze.mercury.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class TextHandler implements TextWatcher {

    private final TextInputLayout textInputLayout;

    public TextHandler(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public static boolean isTextInputFilled(TextInputEditText textInputEditText, TextInputLayout textInputLayout, Context context) {
        if (Objects.requireNonNull(textInputEditText.getText()).toString().trim().isEmpty()) {
            textInputLayout.setError(context.getString(R.string.textHandlerEmpty));
            textInputLayout.setErrorEnabled(true);
            return false;
        }
        return true;
    }

    public static boolean isTextInputLengthCorrect(TextInputEditText textInputEditText, TextInputLayout textInputLayout, int number, Context context) {
        if (isTextInputFilled(textInputEditText, textInputLayout, context)) {
            if (Objects.requireNonNull(textInputEditText.getText()).toString().length() > number) {
                textInputLayout.setError(context.getString(R.string.textHandlerTooManyCharacters));
                textInputLayout.setErrorEnabled(true);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isTextInputsEqual(TextInputEditText textInputEditText, TextInputEditText textInputEditText2, TextInputLayout textInputLayout, TextInputLayout textInputLayout2, Context context) {
        String firstInput = Objects.requireNonNull(textInputEditText.getText()).toString().trim();
        String secondInput = Objects.requireNonNull(textInputEditText2.getText()).toString().trim();

        if (!firstInput.equals(secondInput)) {
            textInputLayout.setError(context.getString(R.string.textHandlerNotEqual));
            textInputLayout.setErrorEnabled(true);
            textInputLayout2.setError(context.getString(R.string.textHandlerNotEqual));
            textInputLayout2.setErrorEnabled(true);
            return false;
        }
        return true;
    }

    public static boolean isTextInputsDifferent(TextInputEditText textInputEditText1, TextInputLayout textInputLayout1, TextInputEditText textInputEditText2, TextInputLayout textInputLayout2, Context context) {
        String firstInput = Objects.requireNonNull(textInputEditText1.getText()).toString().trim();
        String secondInput = Objects.requireNonNull(textInputEditText2.getText()).toString().trim();

        if (firstInput.equals(secondInput)) {
            textInputLayout1.setError(context.getString(R.string.textHandlerSameCells));
            textInputLayout1.setErrorEnabled(true);
            textInputLayout2.setError(context.getString(R.string.textHandlerSameCells));
            textInputLayout2.setErrorEnabled(true);
            return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() != 0) {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}