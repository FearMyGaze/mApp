package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.IFireCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Search implements SearchDao {

    private final Context context;
    private final FirebaseFirestore database;

    public Search(Context context) {
        this.context = context;
        this.database = FirebaseFirestore.getInstance();
    }

    @Override
    public void search(String input, IFireCallback<List<User1>> callback) {
        Query query;
        String param = input.trim();

        if (param.startsWith("loc:") && param.length() >= 7) {
            param = param.replace("loc:", "").toLowerCase();
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("locationL", param)
                    .whereLessThanOrEqualTo("locationL", param + "\uf8ff");
        } else if (param.startsWith("job:") && param.length() >= 7) {
            param = param.replace("job:", "").toLowerCase();
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("jobL", param)
                    .whereLessThanOrEqualTo("jobL", param + "\uf8ff");
        } else if (param.startsWith("web:") && param.length() >= 7) {
            param = param.replace("web:", "");
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("website", param)
                    .whereLessThanOrEqualTo("website", param + "\uf8ff");
        } else if (param.length() > 2) {
            param = param.toLowerCase();
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("usernameL", param)
                    .whereLessThanOrEqualTo("usernameL", param + "\uf8ff");
        } else {
            callback.onError("We need more stuff to search");
            return;
        }

        query.limit(40)
                .get()
                .addOnFailureListener(e -> callback.onError("Failed to search for users"))
                .addOnSuccessListener(documents -> {
                    if (!documents.isEmpty()) {
                        List<User1> users = new ArrayList<>();
                        for (DocumentSnapshot document : documents.getDocuments()) {
                            users.add(document.toObject(User1.class));
                        }
                        callback.onSuccess(users);
                    } else {
                        callback.onError("We didn't find users with the given criteria");
                    }
                });
    }

    @Override
    public void getUserById(String userId, CallBackResponse<User1> callback) {
        database.collection("users")
                .document(userId)
                .get()
                .addOnFailureListener(e -> callback.onFailure("Error while executing your request"))
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        User1 user = document.toObject(User1.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onError("There was an unexpected problem getting your user");
                        }
                    } else {
                        callback.onError("We didn't find the user you asked for");
                    }
                });
    }

    @Override
    public void getUserByUsername(String handle, CallBackResponse<User1> callback) {
        database.collection("users")
                .whereEqualTo("handleL", handle.trim().toLowerCase())
                .limit(1)
                .get()
                .addOnFailureListener(e -> callback.onFailure("Error while getting your requested user"))
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        User1 user = querySnapshot.getDocuments().get(0).toObject(User1.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onError("There was an unexpected problem getting your user");
                        }
                    } else {
                        callback.onError("We didn't find the user you asked for");
                    }
                });
    }

}
