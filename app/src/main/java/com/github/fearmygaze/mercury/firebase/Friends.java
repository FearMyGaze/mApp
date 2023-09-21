package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import androidx.annotation.IntRange;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Friends {

    public static final int OPTION_ACCEPT = 0, OPTION_REMOVE = 1;

    public static Query friendsQuery(User user) {
        return FirebaseFirestore.getInstance()
                .collection(Request.COLLECTION)
                .whereArrayContains(Request.BETWEEN, user.getId())
                .whereEqualTo(Request.STATUS, Request.S_FRIEND)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static Query waitingQuery(User user) {
        return FirebaseFirestore.getInstance()
                .collection(Request.COLLECTION)
                .whereEqualTo(Request.RECEIVER, user.getId())
                .whereEqualTo(Request.STATUS, Request.S_WAITING)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static Query blockedQuery(User user) {
        return FirebaseFirestore.getInstance()
                .collection(Request.COLLECTION)
                .whereEqualTo(Request.SENDER, user.getId())
                .whereEqualTo(Request.STATUS, Request.S_BLOCKED)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static void getStatus(User fromUser, User toUser, Context context, OnDataResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereIn(Request.SENDER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .whereIn(Request.RECEIVER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Request request = querySnapshot.getDocuments().get(0).toObject(Request.class);
                        if (request != null) {
                            switch (request.getStatus()) {
                                case Request.S_FRIEND:
                                    listener.onSuccess(0, context.getString(R.string.requestAccepted));
                                    break;
                                case Request.S_WAITING:
                                    if (request.getReceiver().equals(fromUser.getId())) {
                                        listener.onSuccess(0, context.getString(R.string.requestAnswer));
                                    } else {
                                        listener.onSuccess(0, context.getString(R.string.requestWaiting));
                                    }
                                    break;
                                case Request.S_BLOCKED:
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
        DocumentReference docReference = FirebaseFirestore.getInstance()
                .collection(Request.COLLECTION)
                .document();
        docReference
                .set(new Request(docReference.getId(),
                        Request.S_WAITING,
                        fromUser.getId(),
                        toUser.getId(),
                        Request.createRefers(fromUser, toUser),
                        Request.createProfile(fromUser),
                        Request.createProfile(toUser)))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void cancelRequest(User fromUser, User toUser, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(Request.COLLECTION)
                .whereArrayContains(Request.BETWEEN, Request.createRefers(fromUser, toUser))
                .whereEqualTo(Request.STATUS, Request.S_WAITING)
                .limit(1)
                .get()
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
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        reference
                .whereIn(Request.SENDER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .whereIn(Request.RECEIVER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Request request = document.toObject(Request.class);
                        if (request != null) {
                            if (!request.getStatus().equals(Request.S_BLOCKED)) {
                                document.getReference()
                                        .delete()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused -> {
                                            DocumentReference docReference = reference.document();
                                            docReference
                                                    .set(new Request(docReference.getId(),
                                                            Request.S_BLOCKED,
                                                            fromUser.getId(),
                                                            toUser.getId(),
                                                            Request.createRefers(fromUser, toUser),
                                                            Request.createProfile(fromUser),
                                                            Request.createProfile(toUser)))
                                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                    .addOnSuccessListener(unused1 -> listener.onSuccess(0));
                                        });
                            }
                        }
                    } else {
                        DocumentReference docReference = reference.document();
                        docReference
                                .set(new Request(docReference.getId(),
                                        Request.S_BLOCKED,
                                        fromUser.getId(),
                                        toUser.getId(),
                                        Request.createRefers(fromUser, toUser),
                                        Request.createProfile(fromUser),
                                        Request.createProfile(toUser)))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    public static void removeBlock(Request request, Context context, OnResponseListener listener) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(Request.COLLECTION);
        reference
                .document(request.getId())
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Request localRequest = snapshot.toObject(Request.class);
                        if (localRequest != null && localRequest.getStatus().equals(Request.S_BLOCKED)) {
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
                                if (request.getStatus().equals(Request.S_WAITING)) {
                                    documentSnapshot
                                            .getReference()
                                            .update(Request.STATUS, Request.S_FRIEND)
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
                .whereEqualTo(Request.SENDER, otherUser.getId())
                .whereEqualTo(Request.RECEIVER, user.getId())
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
                                    if (request.getStatus().equals(Request.S_WAITING)) {
                                        snapshot.getReference()
                                                .update(Request.STATUS, Request.S_FRIEND)
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
