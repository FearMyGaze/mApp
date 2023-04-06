package com.github.fearmygaze.mercury.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
@Entity(tableName = "users")
public class User { //TODO: we need to add Index inside the colum info

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userUID")
    public String userUID;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "imageURL")
    public String imageURL;

    @ColumnInfo(name = "token")
    public String notificationToken;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "job")
    public String job;

    @ColumnInfo(name = "website")
    public String website;

    @ColumnInfo(name = "createdAt")
    public Long createdAt;

    @ColumnInfo(name = "showFriends")
    public boolean showFriends;

    public User() {
    }

    @Ignore//Generic
    public User(@NonNull String userUID, String username, String name, String imageURL, boolean showFriends) {
        this.userUID = userUID;
        this.username = username;
        this.name = name;
        this.imageURL = imageURL;
        this.showFriends = showFriends;
    }

    @Ignore //Register
    public User(@NonNull String userUID, String email, String username, String name, String imageURL, String notificationToken) {
        this.userUID = userUID;
        this.email = email;
        this.username = username;
        this.name = name;
        this.imageURL = imageURL;
        this.notificationToken = notificationToken;
        this.status = "";
        this.location = "";
        this.job = "";
        this.website = "";
        this.showFriends = true;
    }

    @Ignore //SignIn or Update Info
    public User(@NonNull String userUID, String email, String username, String name, String imageURL, String token, String status, String location, String job, String website, boolean showFriends, Long createdAt) {
        this.userUID = userUID;
        this.email = email;
        this.username = username;
        this.name = name;
        this.imageURL = imageURL;
        this.notificationToken = token;
        this.status = status;
        this.location = location;
        this.job = job;
        this.website = website;
        this.showFriends = showFriends;
        this.createdAt = createdAt;
    }

    @Exclude
    public Map<String, Object> toMap(boolean setTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userUID", this.userUID);
        map.put("email", this.email);
        map.put("username", this.username);
        map.put("name", this.name);
        map.put("imageURL", this.imageURL);
        map.put("notificationToken", this.notificationToken);
        map.put("showFriends", this.showFriends);
        if (status != null && !status.isEmpty())
            map.put("status", this.status);
        if (status != null && !location.isEmpty())
            map.put("location", this.location);
        if (status != null && !job.isEmpty())
            map.put("job", this.job);
        if (status != null && !website.isEmpty())
            map.put("website", this.website);
        if (setTime) map.put("createdAt", ServerValue.TIMESTAMP);
        else map.put("createdAt", this.createdAt);
        return map;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUID='" + userUID + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", notificationToken='" + notificationToken + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", job='" + job + '\'' +
                ", website='" + website + '\'' +
                ", createdAt=" + createdAt +
                ", showFriends=" + showFriends +
                '}';
    }
}