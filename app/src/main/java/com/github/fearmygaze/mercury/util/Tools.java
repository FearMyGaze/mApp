package com.github.fearmygaze.mercury.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.activity.Chat;
import com.github.fearmygaze.mercury.view.activity.Profile;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class Tools {

    public static void setErrorToLayout(TextInputLayout layout, String message, boolean enabled) {
        layout.setErrorEnabled(enabled);
        layout.setError(message);
    }

    public static void closeKeyboard(Context context) {
        View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Move this to the Room controller
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

    public static void createSettingsPreference(Context context) {
        PrivatePreference preference = new PrivatePreference(context);
        if (!preference.contains("showImages")) {
            preference.putBoolean("showImages", true);
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
        return new Intent(Intent.ACTION_PICK)
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }

    public static void goToProfile(String myID, Context context, Activity activity) {
        context.startActivity(new Intent(context, Profile.class)
                .putExtra(User.ID, myID));
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void goToProfileViewer(String myID, User user, Context context) {
        context.startActivity(new Intent(context, ProfileViewer.class)
                .putExtra(User.ID, myID)
                .putExtra("userData", user));
    }

    public static void goToChat(User user, Room room, Context context, Activity activity) {
        context.startActivity(new Intent(context, Chat.class)
                .putExtra("user", user)
                .putExtra("room", room));
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static RequestBuilder<Drawable> profileImage(String image, Context context) {
        return Glide.with(context)
                .load(image)
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background))
                .error(AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background))
                .centerCrop();
    }

    public static String createFileNameWithExtension(Uri image, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH);

        String fileName = dateFormat.format(calendar.getTime());

        if (ContentResolver.SCHEME_CONTENT.equals(image.getScheme())) {
            return fileName + "_" + new Random().nextInt() + "." + mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(image));
        } else {
            return fileName + "_" + new Random().nextInt() + "." + MimeTypeMap.getFileExtensionFromUrl(image.toString());
        }
    }

    public static String addHttp(@NonNull String value) {
        if (!value.startsWith("http://www.") && !value.startsWith("https://www.")) {
            return "https://" + value;
        } else if (!value.startsWith("http://") && !value.startsWith("https://")) {
            return "https://www." + value;
        } else return value;
    }
}
