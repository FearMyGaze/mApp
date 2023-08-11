package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class Friends {


    public static final int OPTION_ACCEPT = 0, OPTION_REMOVE = 1;

    public static void getFriendsList(User user, Context context, OnUsersResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        if (user.getIsProfileOpen()) {
            Task<List<QuerySnapshot>> combinedTask = Tasks.
                    whenAllSuccess(
                            reference.whereEqualTo(Request.RECEIVER_ID, user.getId())
                                    .whereEqualTo(Request.STATUS, Request.ACCEPTED)
                                    .orderBy(Request.CREATED, Query.Direction.ASCENDING)
                                    .get(),
                            reference.whereEqualTo(Request.SENDER_ID, user.getId())
                                    .whereEqualTo(Request.STATUS, Request.ACCEPTED)
                                    .orderBy(Request.CREATED, Query.Direction.ASCENDING)
                                    .get()
                    );
            combinedTask
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                    .addOnSuccessListener(querySnapshots -> {
                        //getUsersFromRequests(user.getId(), Request.createRequests(querySnapshots), context, listener);
                    });
        } else listener.onSuccess(1, null);
    }

    public static Query waitingQuery(User user) {
        return FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereEqualTo(Request.RECEIVER_ID, user.getId()).whereEqualTo(Request.STATUS, Request.WAITING)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static Query blockedQuery(User user) {
        return FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereEqualTo(Request.SENDER_ID, user.getId()).whereEqualTo(Request.STATUS, Request.BLOCKED)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static void requestStatus(String myID, String otherID, Context context, OnDataResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereIn(Request.SENDER_ID, Arrays.asList(myID, otherID)).whereIn(Request.RECEIVER_ID, Arrays.asList(myID, otherID))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Request request = querySnapshot.getDocuments().get(0).toObject(Request.class);
                        if (request != null) {
                            switch (request.getStatus()) {
                                case Request.ACCEPTED:
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

    public static void sendRequest(User myUser, User otherUser, Context context, OnResponseListener listener) {
        DocumentReference docReference = FirebaseFirestore.getInstance().collection(Request.COLLECTION).document();
        docReference
                .set(new Request(docReference.getId(), Request.WAITING,
                        myUser.getId(), myUser.getUsername(), myUser.getImage(),
                        otherUser.getId(), otherUser.getUsername(), otherUser.getImage()))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void cancelRequest(User myUser, User otherUser, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereEqualTo(Request.SENDER_ID, myUser.getId()).whereEqualTo(Request.RECEIVER_ID, otherUser.getId())
                .whereEqualTo(Request.STATUS, Request.WAITING).limit(1)
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                        Request request = snapshot.toObject(Request.class);
                        if (request != null) {
                            snapshot.getReference()
                                    .delete()
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(unused -> listener.onSuccess(0));
                        }
                    } else {
                        listener.onSuccess(-1);
                    }
                });
    }

    public static void block(User myUser, User otherUser, Context context, OnResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        reference
                .whereIn(Request.SENDER_ID, Arrays.asList(myUser.getId(), otherUser.getId())).whereIn(Request.RECEIVER_ID, Arrays.asList(myUser.getId(), otherUser.getId()))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            if (!request.getStatus().equals(Request.BLOCKED)) {
                                request.setStatus(Request.BLOCKED);
                                reference
                                        .document(request.getId())
                                        .set(request)
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                            } else listener.onSuccess(1);
                        }
                    } else {
                        DocumentReference docReference = reference.document();
                        docReference
                                .set(new Request(docReference.getId(), Request.BLOCKED,
                                        myUser.getId(), myUser.getUsername(), myUser.getImage(),
                                        otherUser.getId(), otherUser.getUsername(), otherUser.getImage()))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    public static void unBlock(Request request, Context context, OnResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        reference
                .document(request.getId())
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Request localRequest = snapshot.toObject(Request.class);
                        if (localRequest != null && localRequest.getStatus().equals(Request.BLOCKED)) {
                            snapshot.getReference()
                                    .delete()
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(unused -> listener.onSuccess(0));
                        } else listener.onSuccess(-1);
                    }
                });
    }

    public static void answerRequest(Request request, @IntRange(from = 0, to = 1) int option, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .document(request.getId())
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    Request fetchedRequest = documentSnapshot.toObject(Request.class);
                    if (fetchedRequest != null) {
                        switch (option) {
                            case OPTION_ACCEPT:
                                if (request.getStatus().equals(Request.WAITING)) {
                                    request.setStatus(Request.ACCEPTED);
                                    documentSnapshot
                                            .getReference()
                                            .set(request)
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                            .addOnSuccessListener(unused -> listener.onSuccess(0));
                                } else listener.onSuccess(1);
                                break;
                            case OPTION_REMOVE:
                                documentSnapshot
                                        .getReference()
                                        .delete()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> listener.onSuccess(0));
                                break;
                        }

                    } else listener.onSuccess(-1);
                });
    }

    public static void answerRequest(User user, User otherUser, @IntRange(from = 0, to = 1) int option, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereEqualTo(Request.SENDER_ID, otherUser.getId()).whereEqualTo(Request.RECEIVER_ID, user.getId())
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                        Request request = snapshot.toObject(Request.class);
                        if (request != null) {
                            switch (option) {
                                case OPTION_ACCEPT:
                                    if (request.getStatus().equals(Request.WAITING)) {
                                        request.setStatus(Request.ACCEPTED);
                                        snapshot.getReference()
                                                .set(request)
                                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                                    } else listener.onSuccess(1);
                                    break;
                                case OPTION_REMOVE:
                                    snapshot.getReference()
                                            .delete()
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                            .addOnSuccessListener(unused -> listener.onSuccess(0));
                                    break;
                            }
                        }
                    } else listener.onSuccess(-1);
                });
    }

}
