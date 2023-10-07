package com.github.fearmygaze.mercury.firebase.dao;

import android.annotation.SuppressLint;

import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserDao {

    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore INSTANCE = null;

    private static synchronized FirebaseFirestore getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseFirestore.getInstance() : INSTANCE;
    }

    public static CollectionReference getReference() {
        return getInstance().collection("users");
    }

    public static CollectionReference pDataReference() {
        return getInstance().collection("publicData");
    }

    public static Task<QuerySnapshot> grantUsername(String username) {
        return pDataReference()
                .whereEqualTo("username", username)
                .limit(1)
                .get();
    }

    public static Task<Void> writeUsername(String username) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        return pDataReference()
                .document()
                .set(map);
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
                .update("notificationToken", token);
    }

    public static Task<Void> update(User user) {
        return getReference()
                .document(user.getId())
                .update("image", user.getImage(),
                        "notificationToken", user.getNotificationToken(), //TODO: Maybe remove this
                        "status", user.getStatus(),
                        "location", user.getLocation(),
                        "locationL", user.getLocationL(),
                        "job", user.getJob(),
                        "jobL", user.getJobL(),
                        "website", user.getWebsite());
    }

    public static Query searchByUsername(String search, int limit) {
        return getReference()
                .whereGreaterThanOrEqualTo("usernameL", search.toLowerCase())
                .whereLessThanOrEqualTo("usernameL", search.toLowerCase() + "\uf8ff")
                .limit(limit);
    }

    public static Query searchByWeb(String search, int limit) {
        search = User.addHttp(search.replace("web:", ""));
        return getReference()
                .whereGreaterThanOrEqualTo("website", search)
                .whereLessThanOrEqualTo("website", search + "\uf8ff")
                .limit(limit);
    }

    public static Query searchByJob(String search, int limit) {
        search = search.replace("job:", "");
        return getReference()
                .whereGreaterThanOrEqualTo("jobL", search.toLowerCase())
                .whereLessThanOrEqualTo("jobL", search.toLowerCase() + "\uf8ff")
                .limit(limit);
    }

    public static Query searchByLocation(String search, int limit) {
        search = search.replace("loc:", "");
        return getReference()
                .whereGreaterThanOrEqualTo("locationL", search.toLowerCase())
                .whereLessThanOrEqualTo("locationL", search.toLowerCase() + "\uf8ff")
                .limit(limit);
    }
}
