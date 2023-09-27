package com.github.fearmygaze.mercury.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "cachedProfiles")
public class Profile implements Parcelable {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "image")
    String image;

    public Profile() {
    }

    @Ignore
    public Profile(@NonNull String id, String username, String image) {
        this.id = id;
        this.username = username;
        this.image = image;
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

    public static List<Profile> create(User user, List<User> users) {
        users.add(0, user);
        List<Profile> data = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            Profile metaData = new Profile(
                    users.get(i).getId(),
                    users.get(i).getUsername(),
                    users.get(i).getImage()
            );
            data.add(metaData);
        }
        return data;
    }

    protected Profile(Parcel in) {
        id = in.readString();
        username = in.readString();
        image = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(image);
    }

    @NonNull
    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
