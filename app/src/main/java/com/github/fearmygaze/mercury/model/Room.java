package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.R;
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
import java.util.Objects;

public class Room implements Parcelable {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////
    public static final String PARCEL = "room", IMAGE_COLLECTION = "chatRoomImages/";

    public enum RoomType {Private, Group}

    String roomID;
    String roomName;
    boolean isNameModified;
    String ownerID;
    RoomType roomType;
    boolean isEncrypted;
    String roomValidation;
    List<String> visibleTo;
    List<Profile> profiles;
    Message message;
    @ServerTimestamp
    Date created;

    //TODO: in case we add broadcast channels
    // List<String> allowedToTalk;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Room() {
    }

    /**
     * @param roomID      The unique id of the room
     * @param roomName    The name of the room
     * @param ownerID     The unique id of the owner
     * @param roomType    Flag that states the type of the room
     * @param isEncrypted Flag that states if the room is encrypted or not
     * @param visibleTo   List with the id's that are included in the room
     * @param profiles    List with the profiles that are included in the room
     * @param message     The Message that will be displayed in the card
     */
    public Room(String roomID, String roomName,
                String ownerID, RoomType roomType, boolean isEncrypted,
                List<String> visibleTo, List<Profile> profiles, Message message) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.isNameModified = false;
        this.roomValidation = roomName;
        this.ownerID = ownerID;
        this.roomType = roomType;
        this.isEncrypted = isEncrypted;
        this.visibleTo = visibleTo;
        this.profiles = profiles;
        this.message = message;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isNameModified() {
        return isNameModified;
    }

    public void setNameModified(boolean nameModified) {
        isNameModified = nameModified;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRoomValidation() {
        return roomValidation;
    }

    public void setRoomValidation(String roomValidation) {
        this.roomValidation = roomValidation;
    }

    public List<String> getVisibleTo() {
        return visibleTo;
    }

    public void setVisibleTo(List<String> visibleTo) {
        this.visibleTo = visibleTo;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected Room(Parcel in) {
        roomID = in.readString();
        roomName = in.readString();
        isNameModified = in.readByte() != 0;
        ownerID = in.readString();
        roomType = RoomType.values()[in.readInt()];
        isEncrypted = in.readByte() != 0;
        roomValidation = in.readString();
        visibleTo = in.createStringArrayList();
        profiles = in.createTypedArrayList(Profile.CREATOR);
        message = in.readParcelable(Message.class.getClassLoader());
        created = TimestampConverter.unixToDate(in.readLong());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(roomID);
        parcel.writeString(roomName);
        parcel.writeByte((byte) (isNameModified ? 1 : 0));
        parcel.writeString(ownerID);
        parcel.writeInt(roomType.ordinal());
        parcel.writeByte((byte) (isEncrypted ? 1 : 0));
        parcel.writeString(roomValidation);
        parcel.writeStringList(visibleTo);
        parcel.writeTypedList(profiles);
        parcel.writeParcelable(message, i);
        parcel.writeLong(TimestampConverter.dateToUnix(created));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static String createName(String username, List<Profile> list) {
        if (list.size() == 1) {
            return username + "_" + list.get(0).getUsername();
        }

        return String.format(Locale.getDefault(), "%s, %s + %d",
                username,
                list.get(0).getUsername(),
                (list.size() - 1));
    }

    public static String showName(User user, Room room) {
        if (room.isNameModified() || !room.getRoomType().equals(RoomType.Private)) {
            return room.getRoomName();
        }

        return room.getRoomName()
                .replace(user.getUsername(), "")
                .replace("_", "");
    }

    public static List<String> addVisibleTo(String userID, List<Profile> profiles) {
        List<String> list = new ArrayList<>();
        list.add(0, userID);
        for (int i = 0; i < profiles.size(); i++) {
            list.add(profiles.get(i).getId());
        }
        return list;
    }

    public static List<Profile> addProfiles(Profile user, List<Profile> profiles) {
        profiles.add(0, user);
        return profiles;
    }

    public static List<Profile> getProfileImages(User user, Room room) {
        List<Profile> output = new ArrayList<>();
        List<Profile> profiles = room.getProfiles();
        if (room.getRoomType().equals(RoomType.Private)) {
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

    public static String formatMessage(String userID, Message msg, Context ctx) {
        if (msg == null) {
            return "Send the first message";
        }
        String senderCheck = Objects.equals(userID, msg.sendBy) ?
                ctx.getString(R.string.adapterRoomYou) :
                ctx.getString(R.string.adapterRoomOther);
        switch (msg.type) {
            case IMG:
                return String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        senderCheck, "send an image");
            case SOUND:
                return String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        senderCheck, "send a sound bite");
            default:
                return String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        senderCheck, msg.content);
        }
    }

    public static String formatDate(Message message) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getCreated().getTime()), ZoneId.systemDefault());
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
////            return localDateTime.format(dateTimeFormatter);
//        } else {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(message.getCreated().getTime());
////            return String.format("%s", DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
//        }

        return "99:99"; //TODO: we need to format the date to different types
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
                "roomID='" + roomID + '\'' +
                ", roomName='" + roomName + '\'' +
                ", isNameModified=" + isNameModified +
                ", ownerID='" + ownerID + '\'' +
                ", roomType=" + roomType +
                ", isEncrypted=" + isEncrypted +
                ", roomValidation='" + roomValidation + '\'' +
                ", visibleTo=" + visibleTo +
                ", profiles=" + profiles +
                ", message=" + message +
                ", created=" + created +
                '}';
    }
}
