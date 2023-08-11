package com.github.fearmygaze.mercury.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Request {
    public static final String
            COLLECTION = "requests",
            SENDER_ID = "senderID",
            RECEIVER_ID = "receiverID",
            CREATED = "created",
            STATUS = "status",
            WAITING = "waiting",
            ACCEPTED = "accepted",
            BLOCKED = "blocked";

    String id;
    String status;
    String senderID;
    String senderUsername;
    String senderImage;
    String receiverID;
    String receiverUsername;
    String receiverImage;

    @ServerTimestamp
    Date created;


    public Request() {
    }

    public Request(String id, String status, String senderID, String senderUsername, String senderImage, String receiverID, String receiverUsername, String receiverImage) {
        this.id = id;
        this.status = status;
        this.senderID = senderID;
        this.senderUsername = senderUsername;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.receiverUsername = receiverUsername;
        this.receiverImage = receiverImage;
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

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @NonNull
    @Override
    public String toString() {
        return "Request1{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", senderID='" + senderID + '\'' +
                ", senderName='" + senderUsername + '\'' +
                ", senderImage='" + senderImage + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", receiverName='" + receiverUsername + '\'' +
                ", receiverImage='" + receiverImage + '\'' +
                ", created=" + created +
                '}';
    }
}
