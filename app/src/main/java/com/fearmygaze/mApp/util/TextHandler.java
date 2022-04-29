package com.fearmygaze.mApp.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.fearmygaze.mApp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class TextHandler implements TextWatcher {

    private final TextInputLayout textInputLayout;

    public TextHandler(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public static void isTextInputEmpty(TextInputEditText textInputEditText, TextInputLayout textInputLayout, Context context) {
        if (Objects.requireNonNull(textInputEditText.getText()).toString().isEmpty()) {
            textInputLayout.setError(context.getString(R.string.textHandlerEmpty));
            textInputLayout.setErrorEnabled(true);
        }
    }

    public static boolean IsTextInputsEqual(TextInputEditText textInputEditText, TextInputEditText textInputEditText2, TextInputLayout textInputLayout, Context context) {
        String input1 = Objects.requireNonNull(textInputEditText.getText()).toString().trim();
        String input2 = Objects.requireNonNull(textInputEditText2.getText()).toString().trim();

        if (!input1.equals(input2)) {
            textInputLayout.setError(context.getString(R.string.textHandlerNotEqual));
            textInputLayout.setErrorEnabled(true);
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
