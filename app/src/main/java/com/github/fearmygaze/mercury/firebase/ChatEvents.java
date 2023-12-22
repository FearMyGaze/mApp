package com.github.fearmygaze.mercury.firebase;

import com.github.fearmygaze.mercury.firebase.dao.ChatEventsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomResponseListener;
import com.github.fearmygaze.mercury.model.Room;

public class ChatEvents {

    public static void updateName(Room room, String name, OnResponseListener listener) {
        ChatEventsDao.updateName(room, name)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void getRoomSnapshot(Room room, OnRoomResponseListener listener) {
        ChatEventsDao.getRoom(room)
                .addSnapshotListener((document, err) -> {
                    if (err != null) {
                        listener.onFailure(err.getMessage());
                    }

                    if (document != null && document.exists()) {
                        Room r = document.toObject(Room.class);
                        if (r != null) {
                            listener.onSuccess(0, r);
                        } else {
                            listener.onSuccess(1, null);
                        }
                    } else {
                        listener.onSuccess(1, null);
                    }
                });
    }
}
