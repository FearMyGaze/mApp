package com.github.fearmygaze.mercury.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Room implements Parcelable {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    public enum RoomType {PRIVATE, GROUP, BROADCAST}

    String id,
            name,
            creatorID;
    RoomType type;
    boolean encrypted;

    @ServerTimestamp
    Date created;
    List<String> refers,
            authorized;
    //Authorized is for the people that are
    // allowed to speak based on the type

    List<Profile> profiles;
    Message lastMsg;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Room(String id, String name, String creatorID, RoomType type,
                boolean encrypted, Date created, List<String> refers,
                List<String> authorized, List<Profile> profiles, Message lastMsg) {
        this.id = id;
        this.name = name;
        this.creatorID = creatorID;
        this.type = type;
        this.encrypted = encrypted;
        this.created = created;
        this.refers = refers;
        this.authorized = authorized;
        this.profiles = profiles;
        this.lastMsg = lastMsg;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    public String getId() {
        return id;
    }

    public void setId(String val) {
        this.id = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String val) {
        this.name = val;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String val) {
        this.creatorID = val;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType val) {
        this.type = val;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean val) {
        this.encrypted = val;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date val) {
        this.created = val;
    }

    public List<String> getRefers() {
        return refers;
    }

    public void setRefers(List<String> val) {
        this.refers = val;
    }

    public List<String> getAuthorized() {
        return authorized;
    }

    public void setAuthorized(List<String> val) {
        this.authorized = val;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> val) {
        this.profiles = val;
    }

    public Message getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(Message val) {
        this.lastMsg = val;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    protected Room(Parcel in) {
        id = in.readString();
        name = in.readString();
        creatorID = in.readString();
        type = RoomType.values()[in.readInt()];
        encrypted = in.readByte() != 0;
        refers = in.createStringArrayList();
        authorized = in.createStringArrayList();
        profiles = in.createTypedArrayList(Profile.CREATOR);
        lastMsg = in.readParcelable(Message.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(creatorID);
        parcel.writeInt(type.ordinal());
        parcel.writeByte((byte) (encrypted ? 1 : 0));
        parcel.writeStringList(refers);
        parcel.writeStringList(authorized);
        parcel.writeTypedList(profiles);
        parcel.writeParcelable(lastMsg, flags);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static String createName(User user, List<User> list) {
        if (list.size() == 1) {
            return user.getUsername() + "_" + list.get(0).getUsername();
        } else {
            return user.getUsername() + " +" + list.size();
        }
    }

    public static List<String> addRefers(User user, List<User> users) {
        List<String> list = new ArrayList<>();
        list.add(user.getId());
        for (int i = 0; i < users.size(); i++) {
            list.add(users.get(i).getId());
        }
        return list;
    }

    public static List<Profile> addProfiles(User user, List<User> users) {
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

    public static String modifyName(User user, Room room) {
        if (room.getType().equals(RoomType.GROUP)) {
            return room.getName();
        } else {
            return room.getName()
                    .replace(user.getUsername(), "")
                    .replace("_", "");
        }
    }

    public static String transformMsg() {

        return "";
    }

    public static String transformDate() {
        return "";
    }


    @NonNull
    @Override
    public String toString() {
        return "Room1{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creatorID='" + creatorID + '\'' +
                ", type=" + type +
                ", encrypted=" + encrypted +
                ", created=" + created +
                ", refers=" + refers +
                ", authorized=" + authorized +
                ", profiles=" + profiles +
                ", lastMsg=" + lastMsg +
                '}';
    }
}
