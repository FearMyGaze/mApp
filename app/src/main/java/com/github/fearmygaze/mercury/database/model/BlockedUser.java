package com.github.fearmygaze.mercury.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "blocked")
public class BlockedUser {

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

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public BlockedUser() {
        if (byUserId == null) byUserId = "";
    }

    public BlockedUser(@NonNull String byUserId, String id, String username, String image) {
        this.byUserId = byUserId;
        this.id = id;
        this.username = username;
        this.image = image;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    public String getByUserId() {
        return byUserId;
    }

    public void setByUserId(@NonNull String byUserId) {
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

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public String toString() {
        return "BlockedUsers{" +
                "byUserId='" + byUserId + '\'' +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
