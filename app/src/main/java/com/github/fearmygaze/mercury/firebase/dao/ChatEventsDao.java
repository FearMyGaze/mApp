package com.github.fearmygaze.mercury.firebase.dao;

import android.content.Context;

import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ChatEventsDao {

    private static FirebaseFirestore INSTANCE = null;

    private static synchronized FirebaseFirestore getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseFirestore.getInstance() : INSTANCE;
    }

    public static CollectionReference getReference() {
        return getInstance().collection("chatRooms");
    }

    public static DocumentReference createDocument() {
        return getReference().document();
    }

    public static Task<QuerySnapshot> exists(User user, List<Profile> profiles) {
        return getReference()
                .whereEqualTo("roomCheck", user.getUsername() + "_" + profiles.get(0).getUsername())
                .whereEqualTo("type", Room.RoomType.Private)
                .orderBy("created", Query.Direction.DESCENDING)
                .get();
    }

    public static Query getRooms(User user) {
        return getReference()
                .whereArrayContains("refers", user.getId())
                .orderBy("created", Query.Direction.DESCENDING);
    }

    public static DocumentReference getRoom(Room room) {
        return getReference()
                .document(room.getId());
    }

    public static Task<Void> create(Room.RoomType type, boolean encrypted, User user,
                                    List<Profile> otherUsers, Context ctx) {
        DocumentReference reference = createDocument();
        Room room = new Room(reference.getId(),
                Room.createName(user, type, otherUsers, ctx),
                false,
                user.getId(),
                type,
                encrypted,
                Room.addRefers(user, otherUsers),
                Room.addProfiles(user, otherUsers),
                null);

        return reference.set(room);
    }

    public static Task<Void> delete(Room room) {
        return getReference()
                .document(room.getId())
                .delete();
    }

    public static Task<Void> updateMembers(Room room, List<Profile> profiles, List<String> refers) {
        return getReference()
                .document(room.getId())
                .update("profiles", profiles, "refers", refers);
    }

    public static Task<Void> updateName(Room room, String roomName) {
        return getReference()
                .document(room.getId())
                .update("name", roomName, "nameModified", true);
    }
}
