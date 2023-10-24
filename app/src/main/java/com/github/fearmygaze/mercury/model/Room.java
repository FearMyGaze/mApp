package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Room implements Parcelable {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////
    public static final String PARCEL = "room";

    public enum RoomType {Private, Group}

    String id;
    String name;
    boolean nameModified;
    String owner;
    RoomType type;
    boolean encrypted;

    @ServerTimestamp
    Date created;
    String roomCheck;
    List<String> refers;
    List<Profile> profiles;
    Message lastMsg;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Room() {
    }

    public Room(String id, String name, boolean nameModified,
                String owner, RoomType type, boolean encrypted,
                List<String> refers, List<Profile> profiles,
                Message lastMsg) {
        this.id = id;
        this.name = name;
        this.nameModified = nameModified;
        this.owner = owner;
        this.type = type;
        this.encrypted = encrypted;
        this.roomCheck = name;
        this.refers = refers;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String val) {
        this.owner = val;
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

    public String getRoomCheck() {
        return roomCheck;
    }

    public void setRoomCheck(String val) {
        this.roomCheck = val;
    }

    public List<String> getRefers() {
        return refers;
    }

    public void setRefers(List<String> val) {
        this.refers = val;
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
        nameModified = in.readByte() != 0;
        owner = in.readString();
        type = RoomType.values()[in.readInt()];
        encrypted = in.readByte() != 0;
        created = TimestampConverter.unixToDate(in.readLong());
        refers = in.createStringArrayList();
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
        parcel.writeString(owner);
        parcel.writeInt(type.ordinal());
        parcel.writeByte((byte) (encrypted ? 1 : 0));
        parcel.writeLong(TimestampConverter.dateToUnix(created));
        parcel.writeStringList(refers);
        parcel.writeTypedList(profiles);
        parcel.writeParcelable(lastMsg, flags);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static String createName(User user, RoomType type, List<Profile> list, Context context) {
        if (list.size() == 1) {
            return user.getUsername() + "_" + list.get(0).getUsername();
        }

        return String.format(Locale.getDefault(), "%s, %s + %d",
                user.getUsername(),
                list.get(0).getUsername(),
                (list.size() - 1));
    }

    public static String showName(User user, Room room) {
        if (room.isNameModified() || !room.getType().equals(RoomType.Private)) {
            return room.getName();
        }

        return room.getName()
                .replace(user.getUsername(), "")
                .replace("_", "");
    }

    public static List<String> addRefers(User user, List<Profile> profiles) {
        List<String> list = new ArrayList<>();
        list.add(user.getId());
        for (int i = 0; i < profiles.size(); i++) {
            list.add(profiles.get(i).getId());
        }
        return list;
    }

    public static List<Profile> addProfiles(User user, List<Profile> profiles) {
        profiles.add(0, new Profile(user.getId(), user.username, user.getImage()));
        return profiles;
    }

    public static List<Profile> getProfileImages(User user, Room room) {
        List<Profile> output = new ArrayList<>();
        List<Profile> profiles = room.getProfiles();
        if (room.getType().equals(RoomType.Private)) {
            for (Profile p : profiles) {
                if (!p.getId().equals(user.getId())) {
                    output.add(p);
                }
            }
        } else {
            for (int i = 0; i < 2; i++) {
                output.add(profiles.get(i));
            }
        }
        return output;
    }

    public static String showDate(Room room) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(room.getCreated().getTime()), ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            return localDateTime.format(dateTimeFormatter);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(room.getCreated().getTime());
            return String.format("%s", DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nameModified=" + nameModified +
                ", creatorID='" + owner + '\'' +
                ", type=" + type +
                ", encrypted=" + encrypted +
                ", created=" + created +
                ", roomCheck='" + roomCheck + '\'' +
                ", correlation=" + refers +
                ", profiles=" + profiles +
                ", lastMsg=" + lastMsg +
                '}';
    }
}
