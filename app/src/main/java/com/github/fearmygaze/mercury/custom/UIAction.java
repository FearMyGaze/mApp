package com.github.fearmygaze.mercury.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;

public class UIAction {

    public static void closeKeyboard(Context context) {
        View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static <T> void flushActivityStuck(Context context, Class<T> activity) {
        context.startActivity(new Intent(context, activity)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public static void openToBrowser(Context context, String website) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public static void goToProfileViewer(Context context, User1 myUser, User1 otherUser) {
        context.startActivity(new Intent(context, ProfileViewer.class)
                .putExtra(User.PARCEL, myUser)
                .putExtra(User.PARCEL_OTHER, otherUser));
    }
}
