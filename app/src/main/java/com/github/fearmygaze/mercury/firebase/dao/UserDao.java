package com.github.fearmygaze.mercury.firebase.dao;

import android.annotation.SuppressLint;

import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserDao {

    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore INSTANCE = null;

    private static synchronized FirebaseFirestore getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseFirestore.getInstance() : INSTANCE;
    }

    public static CollectionReference getReference() {
        return getInstance().collection(User.COLLECTION);
    }

    public static CollectionReference pDataReference() {
        return getInstance().collection(User.PUBLIC_DATA);
    }

    public static Task<Void> createUser(String id, String username) {
        return getReference()
                .document(id)
                .set(new User(id, username));
    }

    public static Task<DocumentSnapshot> getUserByID(String userID) {
        return getReference()
                .document(userID)
                .get();
    }

    public static Task<Void> update(String id, String token) {
        return getReference()
                .document(id)
                .update(User.NOTIFICATION, token);
    }

    public static Task<Void> update(User user) {
        return getReference()
                .document(user.getId())
                .update(User.IMAGE, user.getImage(),
                        User.NOTIFICATION, user.getNotificationToken(),
                        User.STATUS, user.getStatus(),
                        User.LOCATION, user.getLocation(),
                        User.LOCATION_LOWERED, user.getLocationL(),
                        User.JOB, user.getJob(),
                        User.JOB_LOWERED, user.getJobL(),
                        User.WEB, user.getWebsite());
    }

    public static Query searchByUsername(String search, int limit) {
        return getReference()
                .whereGreaterThanOrEqualTo(User.USERNAME_LOWERED, search.toLowerCase())
                .whereLessThanOrEqualTo(User.USERNAME_LOWERED, search.toLowerCase() + "\uf8ff")
                .limit(limit);
    }

    public static Query searchByWeb(String search, int limit) {
        search = User.addHttp(search.replace("web:", ""));
        return getReference()
                .whereGreaterThanOrEqualTo(User.WEB, search)
                .whereLessThanOrEqualTo(User.WEB, search + "\uf8ff")
                .limit(limit);
    }

    public static Query searchByJob(String search, int limit) {
        search = search.replace("job:", "");
        return getReference()
                .whereGreaterThanOrEqualTo(User.JOB_LOWERED, search.toLowerCase())
                .whereLessThanOrEqualTo(User.JOB_LOWERED, search.toLowerCase() + "\uf8ff")
                .limit(limit);
    }

    public static Query searchByLocation(String search, int limit) {
        search = search.replace("loc:", "");
        return getReference()
                .whereGreaterThanOrEqualTo(User.LOCATION_LOWERED, search.toLowerCase())
                .whereLessThanOrEqualTo(User.LOCATION_LOWERED, search.toLowerCase() + "\uf8ff")
                .limit(limit);
    }
}
