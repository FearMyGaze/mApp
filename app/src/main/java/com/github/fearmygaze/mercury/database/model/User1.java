package com.github.fearmygaze.mercury.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.github.fearmygaze.mercury.database.Converters;

import java.util.Date;

@Entity(tableName = "users", indices = {@Index(value = {"id"}, unique = true)})
public class User1 implements Parcelable {

    public static final String
            PARCEL = "user",
            PARCEL_OTHER = "userData";

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    //Basic info
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "usernameL")
    String usernameL;

    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "notificationToken")
    String notificationToken;

    @ColumnInfo(name = "isProfileOpen")
    boolean isProfileOpen;

    @ColumnInfo(name = "created")
    Date created;

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

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public User1() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters/Setters
    ///////////////////////////////////////////////////////////////////////////

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

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public boolean isProfileOpen() {
        return isProfileOpen;
    }

    public void setProfileOpen(boolean profileOpen) {
        isProfileOpen = profileOpen;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<User1> CREATOR = new Creator<User1>() {
        @Override
        public User1 createFromParcel(Parcel in) {
            return new User1(in);
        }

        @Override
        public User1[] newArray(int size) {
            return new User1[size];
        }
    };

    private User1(Parcel in) {
        id = in.readString();
        username = in.readString();
        usernameL = in.readString();
        image = in.readString();
        notificationToken = in.readString();
        isProfileOpen = in.readByte() != 0;
        created = Converters.unixToDate(in.readLong());
        bio = in.readString();
        location = in.readString();
        locationL = in.readString();
        job = in.readString();
        jobL = in.readString();
        website = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(usernameL);
        parcel.writeString(image);
        parcel.writeString(notificationToken);
        parcel.writeByte((byte) (isProfileOpen ? 1 : 0));
        parcel.writeLong(Converters.dateToUnix(created));
        parcel.writeString(bio);
        parcel.writeString(location);
        parcel.writeString(locationL);
        parcel.writeString(job);
        parcel.writeString(jobL);
        parcel.writeString(website);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Other
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", usernameL='" + usernameL + '\'' +
                ", image='" + image + '\'' +
                ", notificationToken='" + notificationToken + '\'' +
                ", isProfileOpen=" + isProfileOpen +
                ", created=" + created +
                ", bio=" + bio + '\'' +
                ", location='" + location + '\'' +
                ", locationL='" + locationL + '\'' +
                ", job='" + job + '\'' +
                ", jobL='" + jobL + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
