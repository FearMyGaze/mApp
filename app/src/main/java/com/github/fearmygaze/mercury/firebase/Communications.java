package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomDataListener;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.RoomMetadata;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class Communications {
    public static Query getRooms(User user) {
        return FirebaseFirestore.getInstance().collection(Room.COLLECTION)
                .whereArrayContains(Room.MEMBERS, user.getId());
    }

    public static void roomExists(User user, List<User> members, Context context, OnRoomDataListener listener) {
        if (members.size() == 1) {
            FirebaseFirestore.getInstance().collection(Room.COLLECTION)
                    .whereArrayContains(Room.MEMBERS, user.getId()).whereEqualTo(Room.GROUP, false)
                    .get()
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                    .addOnSuccessListener(querySnapshot -> {
                        Room room = null;
                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            Room localRoom = snapshot.toObject(Room.class);
                            if (localRoom != null && members.get(0).getId().equals(Room.getCorrectID(user, localRoom))) {
                                room = localRoom;
                                break;
                            }
                        }
                        if (room != null) {
                            listener.onSuccess(0, room);
                        } else {
                            createRoom(user, members, context, listener);
                        }
                    });
        } else createRoom(user, members, context, listener);
    }

    private static void createRoom(User user, List<User> users, Context context, OnRoomDataListener listener) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection(Room.COLLECTION).document();
        Room data = new Room(reference.getId(), Room.createName(user, users), user.getId(), users.size() > 1, Room.addMembers(user, users), RoomMetadata.create(user, users));
        reference
                .set(data)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> {
                    data.setId(reference.getId());
                    listener.onSuccess(100, data);
                });
    }

    public static void addMembers() {
    }

    public static void removeMember() {
    }

    public static void deleteRoom() {
    }

}
