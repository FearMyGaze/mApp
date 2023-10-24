package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import com.github.fearmygaze.mercury.firebase.dao.ChatEventsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomResponseListener;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;

import java.util.List;

public class ChatEvents {

    public static void existingRoom(Room.RoomType type, boolean isEncrypted, User user,
                                    List<Profile> profiles, Context ctx, OnRoomResponseListener listener) {
        if (type.equals(Room.RoomType.Private)) {
            ChatEventsDao.exists(user, profiles)
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            Room room = querySnapshot.getDocuments().get(0).toObject(Room.class);
                            if (room != null) {
                                listener.onSuccess(1, room);
                            }
                        } else {
                            createRoom(type, isEncrypted, user, profiles, ctx, new OnResponseListener() {
                                @Override
                                public void onSuccess(int code) {
                                    listener.onSuccess(0, null);
                                }

                                @Override
                                public void onFailure(String message) {
                                    listener.onFailure(message);
                                }
                            });
                        }
                    });
        } else {
            createRoom(type, isEncrypted, user, profiles, ctx, new OnResponseListener() {
                @Override
                public void onSuccess(int code) {
                    listener.onSuccess(0, null);
                }

                @Override
                public void onFailure(String message) {
                    listener.onFailure(message);
                }
            });
        }
    }

    public static void createRoom(Room.RoomType type, boolean isEncrypted, User user,
                                  List<Profile> profiles, Context ctx, OnResponseListener listener) {
        ChatEventsDao.create(type, isEncrypted, user, profiles, ctx)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void deleteRoom(Room room, OnResponseListener listener) {
        ChatEventsDao.delete(room)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void leaveRoom(User user, Room room, OnResponseListener listener) {
        Profile profile = Profile.create(user);
        List<String> refers = room.getRefers();
        List<Profile> members = room.getProfiles();
        members.remove(profile);
        refers.remove(user.getId());
        ChatEventsDao.updateMembers(room, members, refers)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

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
