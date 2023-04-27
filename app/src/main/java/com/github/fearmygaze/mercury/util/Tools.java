package com.github.fearmygaze.mercury.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Tools {

    public static void setErrorToLayout(TextInputLayout layout, String message, boolean enabled) {
        layout.setErrorEnabled(enabled);
        layout.setError(message);
    }

    public static void setTimedErrorToLayout(TextInputLayout layout, String message, boolean enabled, int ms) {
        setErrorToLayout(layout, message, enabled);
        new Handler().postDelayed(() -> setErrorToLayout(layout, null, false), ms);
    }

    public static void closeKeyboard(Context context) {
        View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void openKeyboard(Context context) {
        View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isAcceptingText()) {
            imm.showSoftInput((View) view.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static String setDateTime(long time) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm a");
            return localDateTime.format(dateTimeFormatter);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return String.format("%s, %s", DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()), DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        }
    }

    public static String setDate(long time) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            return localDateTime.format(dateTimeFormatter);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        }
    }

    public static void createSettingsPreference(Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        if (!preference.contains("alternateEnabled")) {
            preference.putBoolean("alternateEnabled", false);
        }
        if (!preference.contains("showIgnored")) {
            preference.putBoolean("showIgnored", false);
        }
        if (!preference.contains("showImages")) {
            preference.putBoolean("showImages", true);
        }
        if (!preference.contains("showFriends")) {
            preference.putBoolean("showFriends", true);
        }
        if (!preference.contains("showPending")) {
            preference.putBoolean("showPending", true);
        }
    }

    public static void writePreference(String key, boolean value, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        preference.putBoolean(key, value);
    }

    public static boolean getPreference(String key, Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        if (preference.contains(key)) {
            return preference.getBoolean(key);
        }
        return false;
    }

    public static Intent imageSelector() {
        return new Intent(Intent.ACTION_PICK).setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }

    public static String removeHttp(@NonNull String value) {
        if (value.startsWith("https://www."))
            return value.replace("https://www.", "");
        if (value.startsWith("http://www."))
            return value.replace("http://www.", "");
        if (value.startsWith("http://"))
            return value.replace("http://", "");
        if (value.startsWith("https://"))
            return value.replace("https://", "");
        if (value.startsWith("www."))
            return value.replace("www.", "");
        return value;
    }
}
