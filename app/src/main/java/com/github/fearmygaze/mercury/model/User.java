package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.database.UserRoomDao;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "users")
public class User implements Parcelable {

    public static final String
            PUBLIC_DATA = "publicData",
            IMAGE_COLLECTION = "profileImages/",
            COLLECTION = "users",
            ID = "id",
            USERNAME = "username",
            IMAGE = "image",
            NOTIFICATION = "notificationToken",
            STATUS = "status",
            LOCATION = "location",
            JOB = "job",
            WEB = "website",
            CREATED = "created",
            PROFILE = "isProfileOpen";


    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    String id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "notificationToken")
    String notificationToken;

    @ColumnInfo(name = "status")
    String status;

    @ColumnInfo(name = "location")
    String location;

    @ColumnInfo(name = "job")
    String job;

    @ColumnInfo(name = "website")
    String website;

    @ServerTimestamp
    @ColumnInfo(name = "Created")
    Date created;

    @ColumnInfo(name = "isProfileOpen")
    boolean isProfileOpen;

    @Exclude
    @Ignore
    boolean isSelected;

    public User() {
    }

    @Ignore //Register
    public User(@NonNull String id, String username, String image) {
        this.id = id;
        this.username = username;
        this.isProfileOpen = true;
        this.image = image;
    }

    @Ignore //UserProfile
    public User(@NonNull String id, String username, String image, boolean isProfileOpen, String status, String location, String job, String website) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.isProfileOpen = isProfileOpen;
        this.status = status;
        this.location = location;
        this.job = job;
        this.website = website;
    }

    @Ignore
    public User(@NonNull String id, String username, String image, String notificationToken, String status, String location, String job, String website, boolean isProfileOpen, Date created) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.notificationToken = notificationToken;
        this.status = status;
        this.location = location;
        this.job = job;
        this.website = website;
        this.isProfileOpen = isProfileOpen;
        this.created = created;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean getIsProfileOpen() {
        return isProfileOpen;
    }

    public void setProfileOpen(boolean profileOpen) {
        isProfileOpen = profileOpen;
    }

    @Exclude
    public boolean isSelected() {
        return isSelected;
    }

    @Exclude
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static User convertFromDocumentAndSave(DocumentSnapshot document, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.update(document.toObject(User.class));
        return dao.getByID(document.getId());
    }

    public static User updateRoomUser(User user, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.update(user);
        return dao.getByID(user.getId());
    }

    public static User updateRoomToken(String id, String token, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateToken(token, id);
        return dao.getByID(id);
    }

    public static User updateRoomImage(String id, Uri link, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateImage(String.valueOf(link), id);
        return dao.getByID(id);
    }

    public static User updateRoomState(String id, boolean state, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateProfileState(state, id);
        return dao.getByID(id);
    }

    public static User getRoomUser(String id, Context context) {
        return AppDatabase.getInstance(context).userDao().getByID(id);
    }

    public static void deleteRoomUser(User user, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.delete(user);
    }

    public static void deleteAllRoomUsers(Context context) {
        AppDatabase.getInstance(context).userDao().deleteAll();
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
            chip.setText(removeHttp(user.getWebsite()));
            chip.setTextColor(context.getColor(R.color.textBold));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_link_24);
            chip.setChipIconTintResource(resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chip.setOnClickListener(v -> context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(addHttp(user.getWebsite())))
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
            chip.setText(setCorrectDateFormat(TimestampConverter.dateToUnix(user.getCreated())));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_calendar_24);
            chip.setChipIconTintResource(resourceId);
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
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            return localDateTime.format(dateTimeFormatter);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        }
    }

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
        isProfileOpen = in.readByte() != 0;
        isSelected = in.readByte() != 0;
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
        parcel.writeByte((byte) (isProfileOpen ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
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
                ", created=" + created +
                ", isProfileOpen=" + isProfileOpen +
                ", isSelected=" + isSelected +
                '}';
    }
}
