package com.github.fearmygaze.mercury.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "visitedProfiles")
public class VisitedProfile implements Parcelable {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "byUserId")
    String byUserId;

    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "notificationToken")
    String notificationToken;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public VisitedProfile() {
    }

    @Ignore
    public VisitedProfile(@NonNull String byUserId, String id, String username, String image, String notificationToken) {
        this.byUserId = byUserId;
        this.id = id;
        this.username = username;
        this.image = image;
        this.notificationToken = notificationToken;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////


    public String getByUserId() {
        return byUserId;
    }

    public void setByUserId(String byUserId) {
        this.byUserId = byUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<VisitedProfile> CREATOR = new Creator<VisitedProfile>() {
        @Override
        public VisitedProfile createFromParcel(Parcel in) {
            return new VisitedProfile(in);
        }

        @Override
        public VisitedProfile[] newArray(int size) {
            return new VisitedProfile[size];
        }
    };

    protected VisitedProfile(Parcel in) {
        byUserId = in.readString();
        id = in.readString();
        username = in.readString();
        image = in.readString();
        notificationToken = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(byUserId);
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(image);
        parcel.writeString(notificationToken);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public String toString() {
        return "VisitedProfile{" +
                "byUserId='" + byUserId + '\'' +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", image='" + image + '\'' +
                ", notificationToken='" + notificationToken + '\'' +
                '}';
    }
}
