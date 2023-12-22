package com.github.fearmygaze.mercury.firebase.dao;

import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatEventsDao {

    private static FirebaseFirestore INSTANCE = null;

    private static synchronized FirebaseFirestore getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseFirestore.getInstance() : INSTANCE;
    }

    public static CollectionReference getReference() {
        return getInstance().collection("chatRooms");
    }

    public static Query getRooms(User user) {
        return getReference()
                .whereArrayContains("visibleTo", user.getId())
                .orderBy("created", Query.Direction.DESCENDING);
    }

    public static DocumentReference getRoom(Room room) {
        return getReference()
                .document(room.getRoomID());
    }

    public static Task<Void> updateName(Room room, String roomName) {
        return getReference()
                .document(room.getRoomID())
                .update("name", roomName, "nameModified", true);
    }
}
