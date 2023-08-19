package com.github.fearmygaze.mercury.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "cachedProfiles")
public class CachedProfile {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "aa")
    long aa;

    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "image")
    String image;

    public CachedProfile() {
    }

    @Ignore
    public CachedProfile(String id, String username, String image) {
        this.id = id;
        this.username = username;
        this.image = image;
    }

    public void setAa(long aa) {
        this.aa = aa;
    }

    public long getAa() {
        return aa;
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

    @Override
    public String toString() {
        return "CachedProfile{" +
                "aa=" + aa +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
