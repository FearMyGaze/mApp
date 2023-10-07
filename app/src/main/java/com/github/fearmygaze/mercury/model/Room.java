package com.github.fearmygaze.mercury.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoom implements Parcelable {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    public enum RoomType {PRIVATE, GROUP, BROADCAST}

    String id;
    String name;
    boolean nameModified;
    String creatorID;
    RoomType type;
    boolean encrypted;

    @ServerTimestamp
    Date created;
    List<String> included;
    List<String> allowedToTalk;
    List<Profile> profiles;
    Message lastMsg;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public ChatRoom() {
    }

    public ChatRoom(String id, String name, boolean nameModified,
                    String creatorID, RoomType type, boolean encrypted,
                    List<String> included, List<String> allowedToTalk, List<Profile> profiles,
                    Message lastMsg) {
        this.id = id;
        this.name = name;
        this.nameModified = nameModified;
        this.creatorID = creatorID;
        this.type = type;
        this.encrypted = encrypted;
        this.included = included;
        this.allowedToTalk = allowedToTalk;
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

    public boolean isNameModified() {
        return nameModified;
    }

    public void setNameModified(boolean val) {
        this.nameModified = val;
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

    public List<String> getIncluded() {
        return included;
    }

    public void setIncluded(List<String> val) {
        this.included = val;
    }

    public List<String> getAllowedToTalk() {
        return allowedToTalk;
    }

    public void setAllowedToTalk(List<String> val) {
        this.allowedToTalk = val;
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

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

    protected ChatRoom(Parcel in) {
        id = in.readString();
        name = in.readString();
        nameModified = in.readByte() != 0;
        creatorID = in.readString();
        type = RoomType.values()[in.readInt()];
        encrypted = in.readByte() != 0;
        included = in.createStringArrayList();
        allowedToTalk = in.createStringArrayList();
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
        parcel.writeByte((byte) (nameModified ? 1 : 0));
        parcel.writeString(creatorID);
        parcel.writeInt(type.ordinal());
        parcel.writeByte((byte) (encrypted ? 1 : 0));
        parcel.writeStringList(included);
        parcel.writeStringList(allowedToTalk);
        parcel.writeTypedList(profiles);
        parcel.writeParcelable(lastMsg, flags);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static String createName(User user, List<User> list) {
        if (list.size() == 1) {
            return user.getUsername() + "_" + list.get(0).getUsername();
        }

        StringBuilder s = new StringBuilder(user.getUsername());
        for (User u : list) {
            s.append(",").append(u.getUsername());
        }
        return s.toString();
    }

    public static String showName(User user, ChatRoom chatRoom) {
        if (chatRoom.isNameModified()) {
            return chatRoom.getName();
        }

        if (chatRoom.getType().equals(RoomType.GROUP)) {
            return chatRoom.getName();
        }

        return chatRoom.getName()
                .replace(user.getUsername(), "")
                .replace("_", "");

    }

    public static List<String> addIncluded(User user, List<User> users) {
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

    public static List<String> addAllowedToTalk(User user) {
        List<String> list = new ArrayList<>();
        list.add(user.getId());
        return list;
    }

    public static List<String> addAllowedToTalk(List<String> oldList, List<User> updatedList) {
        for (User user : updatedList) {
            oldList.add(user.getId());
        }
        return oldList;
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
                ", refers=" + included +
                ", authorized=" + allowedToTalk +
                ", profiles=" + profiles +
                ", lastMsg=" + lastMsg +
                '}';
    }
}
