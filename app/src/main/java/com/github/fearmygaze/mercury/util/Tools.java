package com.github.fearmygaze.mercury.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.textfield.TextInputLayout;

public class Tools {

    public static void setErrorToLayout(TextInputLayout layout, String message, boolean enabled) {
        layout.setErrorEnabled(enabled);
        layout.setError(message);
    }

    public static void createSettingsPreference(String id, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        if (!preference.contains("showImages")) {
            preference.putBoolean("showImages", true);
        }
        preference.putString("current", id);

    }

    public static void writePreference(String key, boolean value, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        preference.putBoolean(key, value);
    }

    public static void writeStrPreference(String key, String value, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        preference.putString(key, value);
    }

    public static boolean getBoolPreference(String key, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        if (preference.contains(key)) {
            return preference.getBoolean(key);
        }
        return false;
    }

    public static String getStrPreference(String key, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        if (preference.contains(key)) {
            return preference.getString(key);
        }
        return null;
    }

    public static Intent imageSelector() {
        return new Intent(Intent.ACTION_PICK)
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }

    public static void goToProfileViewer(User myUser, User user, Context context) {
        context.startActivity(new Intent(context, ProfileViewer.class)
                .putExtra(User.PARCEL, myUser)
                .putExtra(User.PARCEL_OTHER, user)); //TODO: If we simplify the adapter i am gonna remove this
    }

    public static RequestBuilder<Drawable> profileImage(String image, Context context) {
        return Glide.with(context)
                .load(image)
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background))
                .error(AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background))
                .centerCrop();
    }

}
