package com.github.fearmygaze.mercury.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RoomMetaData implements Parcelable {

    String id;
    String username;
    String image;

    public RoomMetaData() {
    }

    public RoomMetaData(String id, String username, String image) {
        this.id = id;
        this.username = username;
        this.image = image;
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

    public static List<RoomMetaData> create(User user, List<User> users) {
        users.add(0, user);
        List<RoomMetaData> data = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            RoomMetaData metaData = new RoomMetaData(users.get(i).getId(), users.get(i).getUsername(), users.get(i).getImage());
            data.add(metaData);
        }
        return data;
    }

    protected RoomMetaData(Parcel in) {
        id = in.readString();
        username = in.readString();
        image = in.readString();
    }

    public static final Creator<RoomMetaData> CREATOR = new Creator<RoomMetaData>() {
        @Override
        public RoomMetaData createFromParcel(Parcel in) {
            return new RoomMetaData(in);
        }

        @Override
        public RoomMetaData[] newArray(int size) {
            return new RoomMetaData[size];
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
        return "RoomMetaData{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
