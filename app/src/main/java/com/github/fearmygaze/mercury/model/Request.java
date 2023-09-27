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

    String id;
    String status;
    String sender;
    String receiver;
    List<String> refers;
    Profile senderProfile;
    Profile receiverProfile;
    @ServerTimestamp
    Date created;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public List<String> getRefers() {
        return refers;
    }

    public void setRefers(List<String> refers) {
        this.refers = refers;
    }

    public Profile getSenderProfile() {
        return senderProfile;
    }

    public void setSenderProfile(Profile senProfile) {
        this.senderProfile = senProfile;
    }

    public Profile getReceiverProfile() {
        return receiverProfile;
    }

    public void setReceiverProfile(Profile recProfile) {
        this.receiverProfile = recProfile;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

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
