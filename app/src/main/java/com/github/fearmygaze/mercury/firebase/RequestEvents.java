package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import com.github.fearmygaze.mercury.firebase.dao.RequestEventsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRequestResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class RequestEvents {

    public static Query friendsQuery(User user) {
        return RequestEventsDao.friends(user);
    }

    public static Query waitingQuery(User user) {
        return RequestEventsDao.waiting(user);
    }

    public static Query blockedQuery(User user) {
        return RequestEventsDao.blocked(user);
    }

    public static void getRequestSnapshot(User fromUser, User toUser, Context context, OnRequestResponseListener listener) {
        RequestEventsDao.getRequest(fromUser, toUser)
                .addSnapshotListener((querySnapshot, error) -> {
                    DocumentSnapshot document;

                    if (error != null) {
                        listener.onFailure(error.getMessage());
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        document = querySnapshot.getDocuments().get(0);
                        if (document != null && document.exists()) {
                            Request request = document.toObject(Request.class);
                            if (request != null) {
                                listener.onSuccess(0, request);
                            } else {
                                listener.onSuccess(1, null);
                            }
                        } else {
                            listener.onSuccess(1, null);
                        }
                    } else {
                        listener.onSuccess(1, null);
                    }

                });
    }

    public static void sendRequest(User fromUser, User toUser, Context context, OnResponseListener listener) {
        RequestEventsDao.createRequest(fromUser, toUser, Request.RequestStatus.Waiting)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void block(User fromUser, User toUser, Context context, OnResponseListener listener) {
        RequestEventsDao.getRequest(fromUser, toUser)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Request fetchedRequest = querySnapshot.getDocuments().get(0).toObject(Request.class);
                        if (fetchedRequest != null && !fetchedRequest.getStatus().equals(Request.RequestStatus.Blocked)) {
                            RequestEventsDao.delete(querySnapshot.getDocuments().get(0))
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(unused -> RequestEventsDao.createRequest(fromUser, toUser, Request.RequestStatus.Blocked)
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                            .addOnSuccessListener(unused1 -> listener.onSuccess(0)));
                        }
                    } else {
                        RequestEventsDao.createRequest(fromUser, toUser, Request.RequestStatus.Blocked)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    public static void removeBlock(Request request, Context context, OnResponseListener listener) {
        RequestEventsDao.getRequest(request)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Request localRequest = documentSnapshot.toObject(Request.class);
                        if (localRequest != null && localRequest.getStatus().equals(Request.RequestStatus.Blocked)) {
                            RequestEventsDao.delete(documentSnapshot)
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(unused -> listener.onSuccess(0));
                        } else listener.onSuccess(-1);
                    }
                });
    }

    public static void accept(Request request, Context context, OnResponseListener listener) {
        RequestEventsDao.getRequest(request)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        RequestEventsDao.updateRequest(documentSnapshot, Request.RequestStatus.Friends)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    public static void delete(Request request, Context context, OnResponseListener listener) {
        RequestEventsDao.getRequest(request)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        RequestEventsDao.delete(documentSnapshot)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

}
