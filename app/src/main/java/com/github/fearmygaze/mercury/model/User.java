package com.github.fearmygaze.mercury.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
@Entity(tableName = "users")
public class User {

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
    public String currentNotificationDeviceTokenID;

    @ColumnInfo(name = "status")
    public String status;

    public User() {
    }

    @Ignore
    public User(String username, String name, String imageURL) {//Search Model
        this.name = name.trim();
        this.username = '@' + username.trim();
        this.imageURL = imageURL;
    }

    @Ignore
    public User(String userUID, String email, String username, String name, String imageURL, String currentNotificationDeviceTokenID) {//Register Model
        this.userUID = userUID.trim();
        this.email = email.trim();
        this.username = username.trim();
        this.name = name.trim();
        this.imageURL = imageURL;
        this.currentNotificationDeviceTokenID = currentNotificationDeviceTokenID.trim();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUID", this.userUID);
        map.put("email", this.email);
        map.put("username", this.username);
        map.put("name", this.name);
        map.put("imageURL", this.imageURL);
        map.put("currentNotificationDeviceTokenID", this.currentNotificationDeviceTokenID);
        return map;
    }

    public Map<String, Object> sendStatus() {
        return Collections.singletonMap("status", this.status);
    }

}