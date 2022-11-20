package com.github.fearmygaze.mercury.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "username")
    private final String username;

    @ColumnInfo(name = "imageUrl")
    private final String imageUrl;

    @ColumnInfo(name = "email")
    private final String email;

    public User(int id, String username, String imageUrl, String email) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User1{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}