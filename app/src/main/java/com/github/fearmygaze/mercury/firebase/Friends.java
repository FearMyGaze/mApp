package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import androidx.annotation.IntRange;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.dao.RequestDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class Friends {

    public static final int OPTION_ACCEPT = 0, OPTION_REMOVE = 1;

    public static Query friendsQuery(User user) {
        return RequestDao.friends(user);
    }

    public static Query waitingQuery(User user) {
        return RequestDao.waiting(user);
    }

    public static Query blockedQuery(User user) {
        return RequestDao.blocked(user);
    }

    public static void getStatus(User fromUser, User toUser, Context context, OnDataResponseListener listener) {
        RequestDao.getStatus(fromUser, toUser)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Request request = querySnapshot.getDocuments().get(0).toObject(Request.class);
                        if (request != null) {
                            switch (request.getStatus()) {
                                case Friends:
                                    listener.onSuccess(0, context.getString(R.string.requestAccepted));
                                    break;
                                case Waiting:
                                    if (request.getReceiver().equals(fromUser.getId())) {
                                        listener.onSuccess(0, context.getString(R.string.requestAnswer));
                                    } else {
                                        listener.onSuccess(0, context.getString(R.string.requestWaiting));
                                    }
                                    break;
                                case Blocked:
                                    listener.onSuccess(0, context.getString(R.string.requestBlocked));
                                    break;
                                default:
                                    listener.onSuccess(0, context.getString(R.string.requestNone));
                                    break;
                            }
                        }
                    } else listener.onSuccess(0, context.getString(R.string.requestNone));
                });
    }

    public static void sendRequest(User fromUser, User toUser, Context context, OnResponseListener listener) {
        RequestDao.createRequest(fromUser, toUser, Request.RequestStatus.Waiting)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void cancelRequest(User fromUser, User toUser, Context context, OnResponseListener listener) {
        RequestDao.cancel(fromUser, toUser)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        Request request = doc.toObject(Request.class);
                        if (request != null) {
                            doc.getReference()
                                    .delete()
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(unused -> listener.onSuccess(0));
                        }
                    } else listener.onSuccess(-1);
                });
    }

    public static void block(User fromUser, User toUser, Context context, OnResponseListener listener) {
        RequestDao.block(fromUser, toUser)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            if (!request.getStatus().equals(Request.RequestStatus.Blocked)) {
                                RequestDao.deleteRequest(document)
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> RequestDao.createRequest(fromUser, toUser, Request.RequestStatus.Blocked)
                                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                .addOnSuccessListener(unused1 -> listener.onSuccess(0)));
                            }
                        }
                    } else {
                        RequestDao.createRequest(fromUser, toUser, Request.RequestStatus.Blocked)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    public static void removeBlock(Request request, Context context, OnResponseListener listener) {
        RequestDao.getRequest(request)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Request localRequest = snapshot.toObject(Request.class);
                        if (localRequest != null && localRequest.getStatus().equals(Request.RequestStatus.Blocked)) {
                            RequestDao.deleteRequest(snapshot)
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(unused -> listener.onSuccess(0));
                        } else listener.onSuccess(-1);
                    }
                });
    }

    public static void answerRequest(Request request, @IntRange(from = 0, to = 1) int option, Context context, OnResponseListener listener) {
        RequestDao.getRequest(request)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    Request fetchedRequest = documentSnapshot.toObject(Request.class);
                    if (fetchedRequest != null) {
                        switch (option) {
                            case OPTION_ACCEPT:
                                if (request.getStatus().equals(Request.RequestStatus.Waiting)) {
                                    RequestDao.updateRequest(documentSnapshot, Request.RequestStatus.Friends)
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                            .addOnSuccessListener(unused -> listener.onSuccess(0));
                                } else listener.onSuccess(1);
                                break;
                            case OPTION_REMOVE:
                                RequestDao.deleteRequest(documentSnapshot)
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                                break;
                        }

                    } else listener.onSuccess(-1);
                });
    }

    public static void answerRequest(User user, User otherUser, @IntRange(from = 0, to = 1) int option, Context context, OnResponseListener listener) {
        RequestDao.answer(user, otherUser)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                        Request request = snapshot.toObject(Request.class);
                        if (request != null) {
                            switch (option) {
                                case OPTION_ACCEPT:
                                    if (request.getStatus().equals(Request.RequestStatus.Waiting)) {
                                        RequestDao.updateRequest(snapshot, Request.RequestStatus.Friends)
                                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                                    } else listener.onSuccess(1);
                                    break;
                                case OPTION_REMOVE:
                                    RequestDao.deleteRequest(snapshot)
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                            .addOnSuccessListener(unused -> listener.onSuccess(0));
                                    break;
                            }
                        }
                    } else listener.onSuccess(-1);
                });
    }

}
