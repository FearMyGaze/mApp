package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "users")
public class User implements Parcelable {

    public static final String
            PARCEL = "user",
            PARCEL_OTHER = "userData";

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    String id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "usernameLowered")
    String usernameL;

    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "notificationToken")
    String notificationToken;

    @ColumnInfo(name = "status")
    String status;

    @ColumnInfo(name = "location")
    String location;

    @ColumnInfo(name = "locationLowered")
    String locationL;

    @ColumnInfo(name = "job")
    String job;

    @ColumnInfo(name = "jobLowered")
    String jobL;

    @ColumnInfo(name = "website")
    String website;

    @ServerTimestamp
    @ColumnInfo(name = "created")
    Date created;

    @ColumnInfo(name = "isProfileOpen")
    boolean isProfileOpen;

    @ColumnInfo(name = "accountType")
    String accountType;

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

    public void setId(@NonNull String val) {
        this.id = val;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String val) {
        this.username = val;
    }

    public String getUsernameL() {
        return usernameL;
    }

    public void setUsernameL(String val) {
        this.usernameL = val;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String val) {
        this.image = val;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String val) {
        this.notificationToken = val;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String val) {
        this.status = val;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String val) {
        this.location = val;
    }

    public String getLocationL() {
        return locationL;
    }

    public void setLocationL(String val) {
        this.locationL = val;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String val) {
        this.job = val;
    }

    public String getJobL() {
        return jobL;
    }

    public void setJobL(String val) {
        this.jobL = val;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String val) {
        this.website = val;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date val) {
        this.created = val;
    }

    public boolean isProfileOpen() {
        return isProfileOpen;
    }

    public void setProfileOpen(boolean val) {
        isProfileOpen = val;
    }

    public void setAccountType(String val) {
        this.accountType = val;
    }

    public String getAccountType() {
        return accountType;
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
        username = in.readString();
        image = in.readString();
        notificationToken = in.readString();
        status = in.readString();
        location = in.readString();
        job = in.readString();
        website = in.readString();
        created = TimestampConverter.unixToDate(in.readLong());
        isProfileOpen = in.readByte() != 0;
        accountType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(image);
        parcel.writeString(notificationToken);
        parcel.writeString(status);
        parcel.writeString(location);
        parcel.writeString(job);
        parcel.writeString(website);
        parcel.writeLong(TimestampConverter.dateToUnix(created));
        parcel.writeByte((byte) (isProfileOpen ? 1 : 0));
        parcel.writeString(accountType);
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
            chip.setChipBackgroundColorResource(R.color.basicBackground);
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
            chip.setChipBackgroundColorResource(R.color.basicBackground);
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
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }

        if (user.getCreated() != null) {
            Chip chip = new Chip(context);
            chip.setText(String.format("%s %s", context.getString(R.string.generalJoined), setCorrectDateFormat(TimestampConverter.dateToUnix(user.getCreated()))));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_calendar_24);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
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

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", image='" + image + '\'' +
                ", notificationToken='" + notificationToken + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", job='" + job + '\'' +
                ", website='" + website + '\'' +
                ", created=" + created + '\'' +
                ", isProfileOpen=" + isProfileOpen + '\'' +
                ", accountType=" + accountType +
                '}';
    }
}
