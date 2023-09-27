package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.github.fearmygaze.mercury.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Room implements Parcelable {

    public static final String
            COLLECTION = "roomData",
            ID = "id",
            NAME = "name",
            CREATOR_ID = "creatorID",
            MEMBERS = "members",
            CREATED = "created",
            GROUP = "isGroup";

    String id;
    String name;
    String creatorID;
    List<String> members;

    @ServerTimestamp
    Date created;

    boolean isGroup;

    @Exclude
    String lastMessage;

    @Exclude
    String lastMessageTime;

    @Exclude
    String lastMessageUserID;

    List<Profile> profiles;

    public Room() {
    }

    //Create
    public Room(String id, String name, String creatorID, boolean isGroup, List<String> members, List<Profile> metaData) {
        this.id = id;
        this.name = name;
        this.creatorID = creatorID;
        this.isGroup = isGroup;
        this.members = members;
        this.profiles = metaData;
    }

    protected Room(Parcel in) {
        id = in.readString();
        name = in.readString();
        creatorID = in.readString();
        members = in.createStringArrayList();
        isGroup = in.readByte() != 0;
        lastMessage = in.readString();
        lastMessageTime = in.readString();
        lastMessageUserID = in.readString();
        profiles = in.createTypedArrayList(Profile.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(creatorID);
        dest.writeStringList(members);
        dest.writeByte((byte) (isGroup ? 1 : 0));
        dest.writeString(lastMessage);
        dest.writeString(lastMessageTime);
        dest.writeString(lastMessageUserID);
        dest.writeTypedList(profiles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    @Exclude
    public String getLastMessage() {
        return lastMessage;
    }

    @Exclude
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Exclude
    public String getLastMessageUserID() {
        return lastMessageUserID;
    }

    @Exclude
    public void setLastMessageUserID(String val) {
        this.lastMessageUserID = val;
    }

    @Exclude
    public String getLastMessageTime() {
        return lastMessageTime;
    }

    @Exclude
    public void setLastMessageTime(String val) {
        this.lastMessageTime = val;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public static String createName(User user, List<User> list) {
        if (list.size() == 1) {
            return user.getUsername() + "_" + list.get(0).getUsername();
        } else {
            return user.getUsername() + " +" + list.size();
        }
    }
    //More like Refers in Requests
    public static List<String> addMembers(User user, List<User> users) {
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

    public static String getCorrectID(User user, Room room) {
        List<String> members = room.getMembers();
        for (int i = 0; i < members.size(); i++) {
            if (!user.getId().equals(members.get(i))) {
                return members.get(i);
            }
        }
        return null;
    }

    public static String modifyName(User user, Room room) {
        return room.getName()
                .replace(user.getUsername(), "")
                .replace("_", "");
    }

    public static String modifyMessage(User user, Room room, Context context) {
        if (room.getLastMessage() != null) {
            if (user.getId().equals(room.getLastMessageUserID())) {
                return context.getString(R.string.adapterRoomYou) + " " + room.getLastMessage();
            } else {
                return context.getString(R.string.adapterRoomOther) + " " + room.getLastMessage();
            }
        }
        return "";
    }

    public static String setDate(long time) {
        return "12:34";
    }


    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creatorID='" + creatorID + '\'' +
                ", members=" + members +
                ", created=" + created +
                ", isGroup=" + isGroup +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime='" + lastMessageTime + '\'' +
                ", lastMessageUserID='" + lastMessageUserID + '\'' +
                ", metaData=" + profiles +
                '}';
    }
}
