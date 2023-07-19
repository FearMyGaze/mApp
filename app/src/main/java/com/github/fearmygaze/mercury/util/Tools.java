package com.github.fearmygaze.mercury.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

    public static String getCorrectDate(long time) {
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

    public static void goToProfileViewer(Context context, String myID, User user) {
        context.startActivity(new Intent(context, ProfileViewer.class)
                .putExtra(User.ID, myID)
                .putExtra("userData", user));
    }

    public static RequestBuilder<Drawable> profileImage(String image, Context context) {
        return Glide.with(context)
                .load(image)
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background))
                .error(AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background))
                .centerCrop();
    }

    public static void extraInfo(User user, boolean showAll, int resourceId, ChipGroup chipGroup, Context context) {
        chipGroup.removeAllViews();

        if (!user.getJob().isEmpty()) {
            Chip chip = new Chip(context);
            chip.setText(user.getJob());
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_repair_service_24);
            chip.setChipIconTintResource(resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }

        if (!user.getWebsite().isEmpty()) {
            Chip chip = new Chip(context);
            chip.setText(Tools.removeHttp(user.getWebsite()));
            chip.setTextColor(context.getColor(R.color.textBold));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_link_24);
            chip.setChipIconTintResource(resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chip.setOnClickListener(v -> context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Tools.addHttp(user.getWebsite())))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
            chipGroup.addView(chip);
        }

        if (!user.getLocation().isEmpty()) {
            Chip chip = new Chip(context);
            chip.setText(user.getLocation());
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_location_24);
            chip.setChipIconTintResource(resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }

        if (showAll && user.getCreated() != null) {
            Chip chip = new Chip(context);
            chip.setText(Tools.getCorrectDate(TimestampConverter.dateToUnix(user.getCreated())));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_calendar_24);
            chip.setChipIconTintResource(resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }
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

    public static String addHttp(@NonNull String value) {
        if (!value.startsWith("http://www.") && !value.startsWith("https://www.")) {
            return "https://" + value;
        } else if (!value.startsWith("http://") && !value.startsWith("https://")) {
            return "https://www." + value;
        } else return value;
    }
}
