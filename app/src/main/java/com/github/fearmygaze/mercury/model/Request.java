package com.github.fearmygaze.mercury.model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Request {

    public static final String
            COLLECTION = "requests",
            ID = "id",
            SENDER = "senderID",
            RECEIVER = "receiverID",
            STATE = "state",
            DATE = "date",
            NONE = "none",
            ACCEPT = "accepted",
            WAITING = "waiting",
            BLOCKED = "blocked";

    @Exclude
    String id;

    String senderID;

    String receiverID;

    String state;

    @ServerTimestamp
    Date date;


    public Request() {
    }

    //SEND
    public Request(String senderID, String receiverID, String state) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.state = state;
    }

    public Request(String id, String senderID, String receiverID, String state, Date date) {
        this.id = id;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.state = state;
        this.date = date;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static String getCorrectID(String myID, Request request) {
        if (myID.equals(request.getSenderID()))
            return request.getReceiverID();
        else
            return request.getSenderID();
    }

    public static List<Request> createRequests(QuerySnapshot querySnapshot) {
        if (querySnapshot != null && !querySnapshot.isEmpty()) {
            List<Request> requests = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Request request = document.toObject(Request.class);
                if (request != null) {
                    request.setId(document.getId());
                    requests.add(request);
                }
            }
            return requests;
        } else return null;
    }

    public static List<Request> createRequests(List<QuerySnapshot> querySnapshots) {
        if (querySnapshots != null && !querySnapshots.isEmpty()) {
            List<DocumentSnapshot> combinedDocuments = new ArrayList<>();
            List<Request> requests = new ArrayList<>();
            for (QuerySnapshot querySnapshot : querySnapshots) {
                combinedDocuments.addAll(querySnapshot.getDocuments());
            }

            for (DocumentSnapshot document : combinedDocuments) {
                Request request = document.toObject(Request.class);
                if (request != null) {
                    request.setId(document.getId());
                    requests.add(request);
                }
            }
            return requests;
        } else return null;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", senderID='" + senderID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", state='" + state + '\'' +
                ", date=" + date +
                '}';
    }
}
