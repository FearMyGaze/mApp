package com.github.fearmygaze.mercury.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Request {

    public enum RequestStatus {Friends, Waiting, Blocked}
    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    String id;
    RequestStatus status;
    String sender;
    String receiver;
    Profile senderProfile;
    Profile receiverProfile;
    List<String> visibleTo;

    @ServerTimestamp
    Date created;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Request() {
    }

    public Request(String id, RequestStatus status, String sender, String receiver,
                   List<String> visibleTo, Profile senderProfile, Profile receiverProfile) {
        this.id = id;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
        this.visibleTo = visibleTo;
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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus val) {
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

    public List<String> getVisibleTo() {
        return visibleTo;
    }

    public void setVisibleTo(List<String> val) {
        this.visibleTo = val;
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

    @NonNull
    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", visibleTo=" + visibleTo +
                ", senderProfile=" + senderProfile +
                ", receiverProfile=" + receiverProfile +
                ", created=" + created +
                '}';
    }
}
