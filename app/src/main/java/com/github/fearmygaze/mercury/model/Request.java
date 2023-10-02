package com.github.fearmygaze.mercury.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Request {

    public static final String
            COLLECTION = "requests",
            SENDER = "sender",
            RECEIVER = "receiver",
            STATUS = "status",
            S_FRIEND = "friends",
            S_WAITING = "waiting",
            S_BLOCKED = "blocked",
            BETWEEN = "refers",
            CREATED = "created";

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    String id;
    String status;
    String sender;
    String receiver;
    List<String> refers;
    Profile senderProfile;
    Profile receiverProfile;
    @ServerTimestamp
    Date created;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Request() {

    }

    public Request(String id, String status, String sender, String receiver, List<String> refers, Profile senderProfile, Profile receiverProfile) {
        this.id = id;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
        this.refers = refers;
        this.senderProfile = senderProfile;
        this.receiverProfile = receiverProfile;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String val) {
        this.status = val;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String val) {
        this.sender = val;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String val) {
        this.receiver = val;
    }

    public List<String> getRefers() {
        return refers;
    }

    public void setRefers(List<String> val) {
        this.refers = val;
    }

    public Profile getSenderProfile() {
        return senderProfile;
    }

    public void setSenderProfile(Profile val) {
        this.senderProfile = val;
    }

    public Profile getReceiverProfile() {
        return receiverProfile;
    }

    public void setReceiverProfile(Profile val) {
        this.receiverProfile = val;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date val) {
        this.created = val;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public static List<String> createRefers(User fromUser, User toUser) {
        List<String> list = new ArrayList<>();
        list.add(fromUser.getId());
        list.add(toUser.getId());
        return list;
    }

    public static Profile createProfile(User user) {
        return new Profile(user.getId(), user.getUsername(), user.getImage());
    }

    @NonNull
    @Override
    public String toString() {
        return "Requests{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", refers=" + refers +
                ", senderProfile=" + senderProfile +
                ", receiverProfile=" + receiverProfile +
                ", created=" + created +
                '}';
    }
}
