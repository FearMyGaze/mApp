package com.github.fearmygaze.mercury.custom;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UIAction {

    //TODO: the custom dialog needs to have the following customizations
    // 1: needs to be cancelable
    // 2: needs to have title and body
    // 3: the option to have 1 or 2 buttons (with the respected colors)

    public static void closeKeyboard(Context context) {
        View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void snackSetup(Context context) {
        //TODO: this is just the setup for the custom SnackBar(see snacking) if it doesnt show correctly use with out icons
        // return a SnackBar
    }

    private void showWarningSnack() {
        //TODO: show the warning snack in companion with `snackSetup`
    }

    private void showSnack() {

    }

    private void showSimpleDialog(String title, String body, String neutralBtn, Context context) {

    }

    private void showComplexDialog(String title, String body, String negativeBtn, String positiveBtn, Context context) {
        //TODO: Create a callback listener and use it to handle the onClicks
    }

}
