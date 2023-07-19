package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntRange;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUsersResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Friends {


    public static final int OPTION_ACCEPT = 0, OPTION_REMOVE = 1;
    public static final int LIST_WAITING = 0, LIST_FOLLOWERS = 1, LIST_BLOCKED = 2;

    public static void getRequestedList(User user, @IntRange(from = 0, to = 2) int option, Context context, OnUsersResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        switch (option) {
            case LIST_WAITING:
                reference
                        .whereEqualTo(Request.RECEIVER, user.getId()).whereEqualTo(Request.STATE, Request.WAITING)
                        .orderBy(Request.DATE, Query.Direction.DESCENDING)
                        .get()
                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                        .addOnSuccessListener(querySnapshot -> getUsersFromRequests(user.getId(), Request.createRequests(querySnapshot), context, listener));
                break;
            case LIST_FOLLOWERS:
                if (user.getIsProfileOpen()) {
                    Task<List<QuerySnapshot>> combinedTask = Tasks.
                            whenAllSuccess(
                                    reference.whereEqualTo(Request.RECEIVER, user.getId()).whereEqualTo(Request.STATE, Request.ACCEPT)
                                            .orderBy(Request.DATE, Query.Direction.ASCENDING).get(),
                                    reference.whereEqualTo(Request.SENDER, user.getId()).whereEqualTo(Request.STATE, Request.ACCEPT)
                                            .orderBy(Request.DATE, Query.Direction.ASCENDING).get()
                            );
                    combinedTask
                            .addOnFailureListener(e -> Log.d("customLog", e.getMessage()))
                            .addOnSuccessListener(querySnapshots -> getUsersFromRequests(user.getId(), Request.createRequests(querySnapshots), context, listener));
                } else listener.onSuccess(1, null);
                break;
            case LIST_BLOCKED:
                reference
                        .whereEqualTo(Request.SENDER, user.getId()).whereEqualTo(Request.STATE, Request.BLOCKED)
                        .orderBy(Request.DATE, Query.Direction.DESCENDING)
                        .get()
                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                        .addOnSuccessListener(querySnapshot -> getUsersFromRequests(user.getId(), Request.createRequests(querySnapshot), context, listener));
                break;
        }
    }

    public static void requestStatus(String myID, String otherID, Context context, OnDataResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereIn(Request.SENDER, Arrays.asList(myID, otherID)).whereIn(Request.RECEIVER, Arrays.asList(myID, otherID))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            request.setId(document.getId());
                            switch (request.getState()) {
                                case Request.ACCEPT:
                                    listener.onSuccess(0, context.getString(R.string.requestAccepted));
                                    break;
                                case Request.WAITING:
                                    listener.onSuccess(0, context.getString(R.string.requestWaiting));
                                    break;
                                case Request.BLOCKED:
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

    public static void sendRequest(String myID, String otherID, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION + "1")
                .document()
                .set(new Request(myID, otherID, Request.WAITING))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void cancelRequest(String myID, String otherID, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereEqualTo(Request.SENDER, myID).whereEqualTo(Request.RECEIVER, otherID)
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                        Request request = snapshot.toObject(Request.class);
                        if (request != null && request.getState().equals(Request.WAITING)) {
                            if (request.getSenderID().equals(myID)) {
                                snapshot
                                        .getReference()
                                        .delete()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                            } else listener.onSuccess(1);
                        } else listener.onSuccess(-1);
                    }
                });
    }

    public static void answerRequest(String myID, String otherID, @IntRange(from = 0, to = 1) int option, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereEqualTo(Request.SENDER, otherID).whereEqualTo(Request.RECEIVER, myID)
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                        Request request = snapshot.toObject(Request.class);
                        switch (option) {
                            case OPTION_ACCEPT:
                                if (request != null && request.getState().equals(Request.WAITING)) {
                                    request.setState(Request.ACCEPT);
                                    snapshot.getReference()
                                            .set(request)
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                            .addOnSuccessListener(unused -> listener.onSuccess(0));
                                }
                                break;
                            case OPTION_REMOVE:
                                querySnapshot
                                        .getDocuments()
                                        .get(0)
                                        .getReference()
                                        .delete()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                                break;
                        }
                    } else listener.onSuccess(-1);
                });
    }

    public static void block(String myID, String otherID, Context context, OnResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        reference
                .whereIn(Request.SENDER, Arrays.asList(myID, otherID)).whereIn(Request.RECEIVER, Arrays.asList(myID, otherID))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            request.setId(document.getId());
                            if (!request.getState().equals(Request.BLOCKED)) {
                                request.setState(Request.BLOCKED);
                                reference
                                        .document(request.getId())
                                        .set(request)
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                            } else listener.onSuccess(1);
                        }
                    } else {
                        reference
                                .document()
                                .set(new Request(myID, otherID, Request.BLOCKED))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    public static void removeBlock(String myID, String otherID, Context context, OnResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        reference
                .whereIn(Request.SENDER, Arrays.asList(myID, otherID)).whereIn(Request.RECEIVER, Arrays.asList(myID, otherID))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            request.setId(document.getId());
                            if (request.getState().equals(Request.BLOCKED)) {
                                document.getReference().delete()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                            } else listener.onSuccess(1);
                        }
                    } else listener.onSuccess(-1);
                });
    }

    private static void getUsersFromRequests(String myID, List<Request> requests, Context context, OnUsersResponseListener listener) {
        if (requests != null && !requests.isEmpty()) {
            List<User> users = new ArrayList<>();
            CollectionReference reference = FirebaseFirestore.getInstance().collection(User.COLLECTION);
            for (int i = 0; i < requests.size(); i++) {
                reference
                        .document(Request.getCorrectID(myID, requests.get(i)))
                        .get()
                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                users.add(User.convertFromDocument(documentSnapshot));
                                listener.onSuccess(0, users);
                            }
                        });
            }
        } else listener.onSuccess(-1, null);
    }

}
