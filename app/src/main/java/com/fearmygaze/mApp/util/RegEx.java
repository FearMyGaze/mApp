package com.fearmygaze.mApp.util;

import android.content.Context;

import com.fearmygaze.mApp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegEx {
    public static boolean isPasswordValid(String passwd, TextInputLayout textInputLayout, Context context) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!#@$%&._])(?=\\S+$).{8,}$");
        Matcher matcher = pattern.matcher(passwd);
        boolean matches = matcher.matches();

        if (!matches) {
            textInputLayout.setError(context.getString(R.string.regExPasswordError));
            textInputLayout.setErrorEnabled(true);
        }

        return matches;
    }   //TODO: Set a better error message

    public static boolean isUsernameValid(String username, TextInputLayout textInputLayout, Context context){
        Pattern pattern = Pattern.compile("[a-zA-Z_0-9]+");
        Matcher matcher = pattern.matcher(username);
        boolean matches = matcher.matches();

        if (!matches){
            textInputLayout.setError(context.getString(R.string.regExUsernameError));
            textInputLayout.setErrorEnabled(true);
        }
        return matches;
    }

    /*
     * Explanation of the following method
     * Length > 0 <= n
     * !errorEnabled()
     * RegEx validation
     * !errorEnabled()
     * Input1 != Input2
     * */

    public static boolean isPasswordValidAndDifferent(TextInputEditText textInputEditText1, TextInputLayout textInputLayout1, TextInputEditText textInputEditText2, TextInputLayout textInputLayout2, int length, Context context) {
        String input1 = Objects.requireNonNull(textInputEditText1.getText()).toString().trim();
        String input2 = Objects.requireNonNull(textInputEditText2.getText()).toString().trim();

        if (TextHandler.isTextInputLengthCorrect(textInputEditText1, textInputLayout1, length, context) && TextHandler.isTextInputLengthCorrect(textInputEditText2, textInputLayout2, length, context)) {
            if (!textInputLayout1.isErrorEnabled() && !textInputLayout2.isErrorEnabled()) {
                if (isPasswordValid(input1, textInputLayout1, context) && isPasswordValid(input2, textInputLayout2, context)) {
                    if (!textInputLayout1.isErrorEnabled() && !textInputLayout2.isErrorEnabled()) {
                        return TextHandler.isTextInputsDifferent(textInputEditText1, textInputLayout1, textInputEditText2, textInputLayout2, context);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
     * Explanation of the following method
     * Length > 0 <= n
     * !errorEnabled()
     * RegEx validation
     * !errorEnabled()
     * Input1 == Input2
     * */

    public static boolean isPasswordValidAndEqual(TextInputEditText textInputEditText1, TextInputLayout textInputLayout1, TextInputEditText textInputEditText2, TextInputLayout textInputLayout2, int length, Context context) {
        String input1 = Objects.requireNonNull(textInputEditText1.getText()).toString().trim();
        String input2 = Objects.requireNonNull(textInputEditText2.getText()).toString().trim();

        if (TextHandler.isTextInputLengthCorrect(textInputEditText1, textInputLayout1, length, context) && TextHandler.isTextInputLengthCorrect(textInputEditText2, textInputLayout2, length, context)) {
            if (!textInputLayout1.isErrorEnabled() && !textInputLayout2.isErrorEnabled()) {
                if (isPasswordValid(input1, textInputLayout1, context) && isPasswordValid(input2, textInputLayout2, context)) {
                    if (!textInputLayout1.isErrorEnabled() && !textInputLayout2.isErrorEnabled()) {
                        return TextHandler.isTextInputsEqual(textInputEditText1, textInputEditText2, textInputLayout1, textInputLayout2, context);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isSignUpFormValid(TextInputEditText textInputEditText1, TextInputLayout textInputLayout1,
                                            TextInputEditText textInputEditText2, TextInputLayout textInputLayout2,
                                            TextInputEditText textInputEditText3, TextInputLayout textInputLayout3,
                                            int length, Context context){

        if (RegEx.isUsernameValid(Objects.requireNonNull(textInputEditText1.getText()).toString().trim(), textInputLayout1,context)){
            return RegEx.isPasswordValidAndEqual(textInputEditText2, textInputLayout2, textInputEditText3, textInputLayout3, length, context);
        }
        return false;
    }

}