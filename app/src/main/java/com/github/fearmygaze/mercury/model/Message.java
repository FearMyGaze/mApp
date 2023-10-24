package com.github.fearmygaze.mercury.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Message implements Parcelable {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    public enum MsgType {TXT, IMG, SOUND, EMBED}

    String id;
    String roomID;
    String sendBy;
    String content; //Either a link or the msg
    List<MsgReaction> reactions;
    MsgType type;

    @ServerTimestamp
    Date created;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Message() {
    }

    public Message(String id, String roomID, String sendBy,
                   String content, MsgType type, Date created) {
        this.id = id;
        this.roomID = roomID;
        this.sendBy = sendBy;
        this.content = content;
        this.type = type;
        this.created = created;
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

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String val) {
        this.roomID = val;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String val) {
        this.sendBy = val;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String val) {
        this.content = val;
    }

    public List<MsgReaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<MsgReaction> val) {
        this.reactions = val;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType val) {
        this.type = val;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date val) {
        this.created = val;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    protected Message(Parcel in) {
        id = in.readString();
        roomID = in.readString();
        sendBy = in.readString();
        content = in.readString();
        reactions = in.createTypedArrayList(MsgReaction.CREATOR);
        type = MsgType.values()[in.readInt()];
        created = TimestampConverter.unixToDate(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(roomID);
        parcel.writeString(sendBy);
        parcel.writeString(content);
        parcel.writeTypedList(reactions);
        parcel.writeInt(type.ordinal());
        parcel.writeLong(TimestampConverter.dateToUnix(created));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static String formatMsgForCard(User user, Message msg, Context ctx) {
        if (msg == null) {
            return "Send the first message";
        }
        String t = Objects.equals(user.getId(), msg.sendBy) ?
                ctx.getString(R.string.adapterRoomYou) :
                ctx.getString(R.string.adapterRoomOther);
        switch (msg.type) {
            case IMG:
                return String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        t, "send an image");
            case SOUND:
                return String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        t, "send a sound bite");
            default:
                return String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        t, msg.content);
        }
    }

    public static String formatMsg(Message msg) {
        return "";
    }

    public static String formatDate(Message msg) {
        if (msg == null || msg.getCreated() == null) {
            return "12:34";
        }
        /*
         * TODO: Steps to show the correct date
         *  1st: hour
         *  2nd: Day
         *  3rd: 13/11/1998 (the rest of the time)
         * */
        return msg.getCreated().toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", sendBy='" + sendBy + '\'' +
                ", content='" + content + '\'' +
                ", reactions='" + reactions + '\'' +
                ", type=" + type +
                ", created=" + created +
                '}';
    }
}
