package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.database.UserRoomDao;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

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

    @Exclude
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
    public User(String username) {
        this.username = username;
        this.isProfileOpen = true;
        this.image = "default";
        this.status = "";
        this.location = "";
        this.job = "";
        this.website = "";
        this.notificationToken = "";
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
    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
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

    public static User convertFromDocument(DocumentSnapshot document) {
        User user = document.toObject(User.class);
        if (user != null) {
            user.setId(document.getId());
        }
        return user;
    }

    public static User convertFromDocumentAndSave(DocumentSnapshot document, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateUser(convertFromDocument(document));
        return dao.getUserByUserID(document.getId());
    }

    public static User updateRoomUser(User user, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateUser(user);
        return dao.getUserByUserID(user.getId());
    }

    public static User updateRoomToken(String id, String token, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateUserToken(token, id);
        return dao.getUserByUserID(id);
    }

    public static User updateRoomImage(String id, Uri link, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateUserImage(String.valueOf(link), id);
        return dao.getUserByUserID(id);
    }

    public static User updateRoomState(String id, boolean state, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.updateProfileState(state, id);
        return dao.getUserByUserID(id);
    }

    public static User getRoomUser(String id, Context context) {
        return AppDatabase.getInstance(context).userDao().getUserByUserID(id);
    }

    public static void deleteRoomUser(User user, Context context) {
        UserRoomDao dao = AppDatabase.getInstance(context).userDao();
        dao.deleteUser(user);
    }

    public static void deleteAllRoomUsers(Context context) {
        AppDatabase.getInstance(context).userDao().deleteAllUsers();
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