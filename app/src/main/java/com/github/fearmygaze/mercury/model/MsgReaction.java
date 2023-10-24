package com.github.fearmygaze.mercury.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MsgReaction implements Parcelable {
    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    public enum ReactionType {LIKE, LOVE} //TODO: Add more

    int count;
    List<Profile> byUsers;
    ReactionType type;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public MsgReaction() {
    }

    public MsgReaction(int count, List<Profile> byUsers, ReactionType type) {
        this.count = count;
        this.byUsers = byUsers;
        this.type = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters/Setters
    ///////////////////////////////////////////////////////////////////////////

    public int getCount() {
        return count;
    }

    public void setCount(int val) {
        this.count = val;
    }

    public List<Profile> getByUsers() {
        return byUsers;
    }

    public void setByUsers(List<Profile> val) {
        this.byUsers = val;
    }

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType val) {
        this.type = val;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected MsgReaction(Parcel in) {
        count = in.readInt();
        byUsers = in.createTypedArrayList(Profile.CREATOR);
        type = ReactionType.values()[in.readInt()];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(count);
        parcel.writeTypedList(byUsers);
        parcel.writeInt(type.ordinal());
    }

    public static final Creator<MsgReaction> CREATOR = new Creator<MsgReaction>() {
        @Override
        public MsgReaction createFromParcel(Parcel in) {
            return new MsgReaction(in);
        }

        @Override
        public MsgReaction[] newArray(int size) {
            return new MsgReaction[size];
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Helper functions
    ///////////////////////////////////////////////////////////////////////////

    public static Message addReaction(User user, MsgReaction.ReactionType type, Message msg) {
        Profile profile = new Profile(user.getId(), user.getUsername(), user.getImage());
        List<MsgReaction> reactionList = msg.getReactions();
        if (reactionList == null || reactionList.isEmpty()) {
            reactionList = new ArrayList<>();
            reactionList.add(new MsgReaction(1, Collections.singletonList(profile), type));
            msg.setReactions(reactionList);
            return msg;
        }
        for (MsgReaction r : reactionList) {
            if (r.getType() == type) {
                r.setCount(r.getCount() + 1);
                r.getByUsers().add(profile);
                msg.setReactions(reactionList);
                return msg;
            }
        }
        return msg;
    }

    public static Message removeReaction(User user, MsgReaction.ReactionType type, Message msg) {
        Profile profile = new Profile(user.getId(), user.getUsername(), user.getImage());
        List<MsgReaction> reactionList = msg.getReactions();
        if (reactionList == null || reactionList.isEmpty()) {
            return msg;
        }
        for (Iterator<MsgReaction> iterator = reactionList.iterator(); iterator.hasNext(); ) {
            MsgReaction reaction = iterator.next();
            if (reaction.getType() == type) {
                if (reaction.getCount() == 1) {
                    iterator.remove();
                } else {
                    reactionList.remove(reaction);
                    reaction.setCount(reaction.getCount() - 1);
                    reaction.getByUsers().remove(profile);
                    reactionList.add(reaction);//TODO: Possibly here we have a mistake
                }
                msg.setReactions(reactionList);
                return msg;
            }
        }
        return msg;
    }

    @NonNull
    @Override
    public String toString() {
        return "MsgReaction{" +
                "count=" + count +
                ", byUsers=" + byUsers +
                ", type=" + type +
                '}';
    }
}
