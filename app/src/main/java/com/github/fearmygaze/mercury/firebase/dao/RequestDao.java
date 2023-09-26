package com.github.fearmygaze.mercury.firebase.dao;

import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class RequestDao {

    private static FirebaseFirestore INSTANCE = null;

    private static synchronized FirebaseFirestore getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseFirestore.getInstance() : INSTANCE;
    }

    public static CollectionReference getReference() {
        return getInstance().collection(Request.COLLECTION);
    }

    public static DocumentReference createDocument() {
        return getReference().document();
    }

    public static Query waiting(User user) {
        return getReference()
                .whereEqualTo(Request.RECEIVER, user.getId())
                .whereEqualTo(Request.STATUS, Request.S_WAITING)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static Query friends(User user) {
        return getReference()
                .whereArrayContains(Request.BETWEEN, user.getId())
                .whereEqualTo(Request.STATUS, Request.S_FRIEND)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static Query blocked(User user) {
        return getReference()
                .whereEqualTo(Request.SENDER, user.getId())
                .whereEqualTo(Request.STATUS, Request.S_BLOCKED)
                .orderBy(Request.CREATED, Query.Direction.DESCENDING);
    }

    public static Task<QuerySnapshot> getStatus(User fromUser, User toUser) {
        return getReference()
                .whereIn(Request.SENDER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .whereIn(Request.RECEIVER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .limit(1)
                .get();
    }

    public static Task<QuerySnapshot> cancel(User fromUser, User toUser) {
        return getReference()
                .whereArrayContains(Request.BETWEEN, Request.createRefers(fromUser, toUser))
                .whereEqualTo(Request.STATUS, Request.S_WAITING)
                .limit(1)
                .get();
    }

    public static Task<QuerySnapshot> answer(User user, User otherUser){
        return getReference()
                .whereEqualTo(Request.SENDER, otherUser.getId())
                .whereEqualTo(Request.RECEIVER, user.getId())
                .limit(1)
                .get();
    }

    public static Task<QuerySnapshot> block(User fromUser, User toUser) {
        return getReference()
                .whereIn(Request.SENDER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .whereIn(Request.RECEIVER, Arrays.asList(fromUser.getId(), toUser.getId()))
                .limit(1)
                .get();
    }
    public static Task<DocumentSnapshot> getRequest(Request request){
        return getReference()
                .document(request.getId())
                .get();
    }
}
