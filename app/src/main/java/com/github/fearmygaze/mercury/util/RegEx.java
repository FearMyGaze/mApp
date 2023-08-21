package com.github.fearmygaze.mercury.util;

import android.content.Context;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegEx {

    public static boolean isPasswordValid(@NonNull TextInputEditText text, TextInputLayout layout, Context context) {
        if (Objects.requireNonNull(text.getText()).toString().isEmpty()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExEmpty), true);
            return false;
        }

        if (text.getText().length() < 8) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExSmaller) + " " + 8, true);
            return false;
        }

        if (text.getText().length() > layout.getCounterMaxLength()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExBigger) + layout.getCounterMaxLength(), true);
            return false;
        }

        Tools.setErrorToLayout(layout, null, false);

        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!#@$%&._])(?=\\S+$).{8,}$");
        Matcher matcher = pattern.matcher(text.getText().toString().trim());

        if (matcher.matches()) return true;
        else {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExPassword), true);
            return false;
        }
    }

    public static boolean isEmailValid(@NonNull TextInputEditText text, TextInputLayout layout, Context context) {
        if (Objects.requireNonNull(text.getText()).toString().isEmpty()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExEmpty), true);
            return false;
        }

        if (text.getText().length() < 8) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExSmaller) + " " + 8, true);
            return false;
        }

        if (text.getText().length() > layout.getCounterMaxLength()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExBigger) + layout.getCounterMaxLength(), true);
            return false;
        }

        Tools.setErrorToLayout(layout, null, false);

        if (Patterns.EMAIL_ADDRESS.matcher(text.getText().toString().trim()).matches()) return true;
        else {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExEmail), true);
            return false;
        }
    }

    public static boolean isUsernameValid(@NonNull TextInputEditText text, TextInputLayout layout, Context context) {
        if (Objects.requireNonNull(text.getText()).toString().isEmpty()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExEmpty), true);
            return false;
        }

        if (text.getText().length() < 8) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExSmaller) + " " + 8, true);
            return false;
        }

        if (text.getText().length() > layout.getCounterMaxLength()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExBigger) + layout.getCounterMaxLength(), true);
            return false;
        }

        Tools.setErrorToLayout(layout, null, false);

        Pattern pattern = Pattern.compile("^[0-9a-zA-ZΑ-Ωα-ω]{6,}?$");
        Matcher matcher = pattern.matcher(text.getText().toString().trim());

        if (matcher.matches()) return true;
        else {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExUsername), true);
            return false;
        }
    }

    public static boolean isUrlValid(TextInputEditText text, TextInputLayout layout, Context context) {
        if (Objects.requireNonNull(text.getText()).toString().isEmpty()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExEmpty), true);
            return false;
        }

        if (text.getText().length() < 8) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExSmaller) + " " + 8, true);
            return false;
        }

        if (text.getText().length() > layout.getCounterMaxLength()) {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExBigger) + layout.getCounterMaxLength(), true);
            return false;
        }

        Tools.setErrorToLayout(layout, null, false);

        if (Patterns.WEB_URL.matcher(text.getText().toString().trim()).matches()) return true;
        else {
            Tools.setErrorToLayout(layout, context.getString(R.string.regExURL), true);
            return false;
        }
    }

}
