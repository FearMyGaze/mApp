package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(tableName = "users", indices = {@Index(value = {"id", "username"}, unique = true)})
public class User implements Parcelable {

    public static final String
            PARCEL = "user",
            PARCEL_OTHER = "userData";

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "fullName")
    String fullName;

    @ColumnInfo(name = "fullNameL")
    String fullNameL;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "usernameL")
    String usernameL;

    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "bio")
    String bio;

    @ColumnInfo(name = "location")
    String location;

    @ColumnInfo(name = "locationL")
    String locationL;

    @ColumnInfo(name = "job")
    String job;

    @ColumnInfo(name = "jobL")
    String jobL;

    @ColumnInfo(name = "website")
    String website;

    @ColumnInfo(name = "isProfileOpen")
    boolean isProfileOpen;

    @ColumnInfo(name = "notificationToken")
    String notificationToken;

    @ColumnInfo(name = "created")
    Date created;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public User() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullNameL() {
        return fullNameL;
    }

    public void setFullNameL(String fullNameL) {
        this.fullNameL = fullNameL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameL() {
        return usernameL;
    }

    public void setUsernameL(String usernameL) {
        this.usernameL = usernameL;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationL() {
        return locationL;
    }

    public void setLocationL(String locationL) {
        this.locationL = locationL;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobL() {
        return jobL;
    }

    public void setJobL(String jobL) {
        this.jobL = jobL;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isProfileOpen() {
        return isProfileOpen;
    }

    public void setProfileOpen(boolean profileOpen) {
        isProfileOpen = profileOpen;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    protected User(Parcel in) {
        id = in.readString();
        fullName = in.readString();
        fullNameL = in.readString();
        username = in.readString();
        usernameL = in.readString();
        image = in.readString();
        notificationToken = in.readString();
        bio = in.readString();
        location = in.readString();
        locationL = in.readString();
        job = in.readString();
        jobL = in.readString();
        website = in.readString();
        created = TimestampConverter.unixToDate(in.readLong());
        isProfileOpen = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(fullName);
        parcel.writeString(fullNameL);
        parcel.writeString(username);
        parcel.writeString(usernameL);
        parcel.writeString(image);
        parcel.writeString(notificationToken);
        parcel.writeString(bio);
        parcel.writeString(location);
        parcel.writeString(locationL);
        parcel.writeString(job);
        parcel.writeString(jobL);
        parcel.writeString(website);
        parcel.writeLong(TimestampConverter.dateToUnix(created));
        parcel.writeByte((byte) (isProfileOpen ? 1 : 0));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static User updateRoomUser(User user, Context context) {
        return AppDatabase.getInstance(context).userDao().transactionUpdateUser(user);
    }

    public static User updateRoomToken(String id, String token, Context context) {
        return AppDatabase.getInstance(context).userDao().transactionUpdateToken(token, id);
    }

    public static User getRoomUser(String id, Context context) {
        return AppDatabase.getInstance(context).userDao().getByID(id);
    }

    public static void deleteRoomUser(User user, Context context) {
        AppDatabase.getInstance(context).userDao().delete(user);
    }

    public static void deleteAllRoomUsers(Context context) {
        AppDatabase.getInstance(context).userDao().deleteAll();
    }

    public static void extraInfo(User user, int resourceId, ChipGroup chipGroup, Context context) {
        chipGroup.removeAllViews();

        if (user.getJob() != null && !user.getJob().isEmpty()) {
            Chip chip = new Chip(context);
            chip.setText(user.getJob());
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_repair_service_24);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chipGroup.addView(chip);
        }

        if (user.getWebsite() != null && !user.getWebsite().isEmpty()) {
            Chip chip = new Chip(context);
            chip.setText(removeHttp(user.getWebsite()));
            chip.setTextColor(context.getColor(resourceId));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_link_24);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chip.setOnClickListener(v -> context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(addHttp(user.getWebsite())))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
            chipGroup.addView(chip);
        }

        if (user.getLocation() != null && !user.getLocation().isEmpty()) {
            Chip chip = new Chip(context);
            chip.setText(user.getLocation());
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_location_24);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chipGroup.addView(chip);
        }

        if (user.getCreated() != null) {
            Chip chip = new Chip(context);
            chip.setText(String.format("%s %s", context.getString(R.string.generalJoined), setCorrectDateFormat(TimestampConverter.dateToUnix(user.getCreated()))));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_calendar_24);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chipGroup.addView(chip);
        }
    }

    public static String removeHttp(@NonNull String link) {
        if (link.startsWith("https://www."))
            return link.replace("https://www.", "");
        if (link.startsWith("http://www."))
            return link.replace("http://www.", "");
        if (link.startsWith("http://"))
            return link.replace("http://", "");
        if (link.startsWith("https://"))
            return link.replace("https://", "");
        if (link.startsWith("www."))
            return link.replace("www.", "");
        return link;
    }

    public static String addHttp(@NonNull String link) {
        if (!link.startsWith("http://www.") && !link.startsWith("https://www.")) {
            return "https://" + link;
        } else if (!link.startsWith("http://") && !link.startsWith("https://")) {
            return "https://www." + link;
        } else return link;
    }

    private static String setCorrectDateFormat(long time) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return localDateTime.format(dateTimeFormatter);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return String.format(Locale.getDefault(),
                    "%02d-%02d-%d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR));
        }
    }

    public static SpannableString formatBio(String inputText, int color, OnTextListener onClickListener) {
        SpannableString spannableString = new SpannableString(inputText);
        for (String regex : RegEx.bio()) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(inputText.trim());

            while (matcher.find()) {
                String matchedText = matcher.group();
                int start = matcher.start();
                int end = matcher.end();

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        if (onClickListener != null) {
                            onClickListener.onClick(matchedText);
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", fullNameL='" + fullNameL + '\'' +
                ", username='" + username + '\'' +
                ", usernameL='" + usernameL + '\'' +
                ", image='" + image + '\'' +
                ", bio='" + bio + '\'' +
                ", location='" + location + '\'' +
                ", locationL='" + locationL + '\'' +
                ", job='" + job + '\'' +
                ", jobL='" + jobL + '\'' +
                ", website='" + website + '\'' +
                ", isProfileOpen=" + isProfileOpen +
                ", notificationToken='" + notificationToken + '\'' +
                ", created=" + created +
                '}';
    }

    public interface OnTextListener {
        void onClick(String text);
    }
}
