package com.github.fearmygaze.mercury.firebase.dao;

import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class RequestEventsDao {

    private static FirebaseFirestore INSTANCE = null;

    private static synchronized FirebaseFirestore getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseFirestore.getInstance() : INSTANCE;
    }

    public static CollectionReference getReference() {
        return getInstance().collection("requests");
    }

    public static DocumentReference createDocument() {
        return getReference().document();
    }

    public static Query waiting(User user) {
        return getReference()
                .whereEqualTo("receiver", user.getId())
                .whereEqualTo("status", Request.RequestStatus.Waiting)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    public static Query friends(User user) {
        return getReference()
                .whereArrayContains("refers", user.getId())
                .whereEqualTo("status", Request.RequestStatus.Friends)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    public static Query blocked(User user) {
        return getReference()
                .whereEqualTo("sender", user.getId())
                .whereEqualTo("status", Request.RequestStatus.Blocked)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    public static Task<Void> createRequest(User fromUser, User toUser, Request.RequestStatus status) {
        DocumentReference reference = createDocument();
        return reference.set(new Request(reference.getId(),
                status,
                fromUser.getId(),
                toUser.getId(),
                Request.createRefers(fromUser, toUser),
                Profile.create(fromUser),
                Profile.create(toUser)));
    }

    public static Query getRequest(User fromUser, User toUser) {
        return getReference()
                .whereIn("sender", Arrays.asList(fromUser.getId(), toUser.getId()))
                .whereIn("receiver", Arrays.asList(fromUser.getId(), toUser.getId()))
                .limit(1);
    }

    public static Task<DocumentSnapshot> getRequest(Request request) {
        return getReference()
                .document(request.getId())
                .get();
    }

    public static Task<Void> updateRequest(DocumentSnapshot snapshot, Request.RequestStatus status) {
        return snapshot
                .getReference()
                .update("status", status);
    }

    public static Task<Void> delete(DocumentSnapshot snapshot) {
        return snapshot
                .getReference()
                .delete();
    }
}
