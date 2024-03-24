package com.github.fearmygaze.mercury.custom;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UIAction {

    public static void closeKeyboard(Context context) {
        View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
