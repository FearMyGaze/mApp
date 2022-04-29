package com.fearmygaze.mApp.util;

import android.content.Context;

import com.fearmygaze.mApp.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegEx {
    public static boolean isPasswordValid(String passwd, TextInputLayout textInputLayout, Context context){
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$");
        Matcher matcher = pattern.matcher(passwd);
        boolean matches = matcher.matches();

        if (!matches){
            textInputLayout.setError(context.getString(R.string.regExPasswordError));
            textInputLayout.setErrorEnabled(true);
        }

        return matches;
    }
}
