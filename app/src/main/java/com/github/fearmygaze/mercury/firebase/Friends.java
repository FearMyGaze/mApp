package com.github.fearmygaze.mercury.firebase;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Friends {

    private static final String BUCKET_FRIENDS = "friends";
    private static final String BUCKET_USERS = "users";
    private static final String USER_VALUE_ID = "userUID";
    private static final String USER_VALUE_USERNAME = "username";
    private static final String USER_VALUE_NAME = "name";
    private static final String USER_VALUE_IMAGE = "imageURL";
    private static final String USER_VALUE_FRIENDS = "showFriends";
    private static final String STATUS_WAITING_RESPONSE = "waiting";
    private static final String STATUS_IGNORED = "ignored";
    private static final String STATUS_FRIENDS = "friends";

    public static void status(String senderID, String receiverID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(receiverID).child(senderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (Objects.equals(snapshot.getValue(String.class), STATUS_FRIENDS)) {
                                listener.onResult(1);
                            } else if (Objects.equals(snapshot.getValue(String.class), STATUS_IGNORED)) {
                                listener.onResult(0);
                            } else {
                                listener.onResult(2);
                            }
                        } else listener.onResult(-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    public static void sendRequest(String senderID, String receiverID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(receiverID).child(senderID)
                .setValue(STATUS_WAITING_RESPONSE)
                .addOnSuccessListener(unused -> listener.onResult(1))
                .addOnCanceledListener(() -> listener.onResult(0))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void cancelRequest(String senderID, String receiverID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(receiverID).child(senderID)
                .removeValue()
                .addOnSuccessListener(unused -> listener.onResult(1))
                .addOnCanceledListener(() -> listener.onResult(0))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void ignoreRequest(String senderID, String receiverID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(senderID).child(receiverID)
                .setValue(STATUS_IGNORED)
                .addOnSuccessListener(unused -> listener.onResult(1))
                .addOnCanceledListener(() -> listener.onResult(0))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void acceptRequest(String senderID, String receiverID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(receiverID).child(senderID).setValue(STATUS_FRIENDS)
                .addOnSuccessListener(unused ->
                        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                                .child(senderID).child(receiverID).setValue(STATUS_FRIENDS)
                                .addOnSuccessListener(unused1 -> listener.onResult(1))
                                .addOnCanceledListener(() -> listener.onResult(0))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                )
                .addOnCanceledListener(() -> listener.onResult(0))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void removeFriend(String senderID, String receiverID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(senderID).child(receiverID)
                .removeValue()
                .addOnSuccessListener(unused ->
                        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                                .child(receiverID).child(senderID)
                                .removeValue()
                                .addOnSuccessListener(unused1 -> listener.onResult(1))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                )
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void friendList(String senderID, OnDataResultListener listener) {
        List<User> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(senderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot user : snapshot.getChildren()) {
                                if (Objects.equals(user.getValue(String.class), STATUS_FRIENDS)) {
                                    FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                                            .orderByChild(USER_VALUE_ID).equalTo(user.getKey())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        DataSnapshot user = snapshot.getChildren().iterator().next();
                                                        list.add(new User(
                                                                Objects.requireNonNull(user.child(USER_VALUE_ID).getValue(String.class)),
                                                                user.child(USER_VALUE_USERNAME).getValue(String.class),
                                                                user.child(USER_VALUE_NAME).getValue(String.class),
                                                                user.child(USER_VALUE_IMAGE).getValue(String.class),
                                                                Boolean.TRUE.equals(user.child(USER_VALUE_FRIENDS).getValue(Boolean.class))
                                                        ));
                                                        listener.onResult(1, list);
                                                    } else listener.onResult(0, null);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    listener.onFailure(error.getMessage());
                                                }
                                            });
                                } else listener.onResult(0, null);
                            }
                        } else listener.onResult(0, null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    public static void pendingList(String senderID, OnDataResultListener listener) {
        List<User> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(senderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot user : snapshot.getChildren()) {
                                if (Objects.equals(user.getValue(String.class), STATUS_WAITING_RESPONSE)) {
                                    FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                                            .orderByChild(USER_VALUE_ID).equalTo(user.getKey())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        DataSnapshot user = snapshot.getChildren().iterator().next();
                                                        list.add(new User(
                                                                Objects.requireNonNull(user.child(USER_VALUE_ID).getValue(String.class)),
                                                                user.child(USER_VALUE_USERNAME).getValue(String.class),
                                                                user.child(USER_VALUE_NAME).getValue(String.class),
                                                                user.child(USER_VALUE_IMAGE).getValue(String.class),
                                                                Boolean.TRUE.equals(user.child(USER_VALUE_FRIENDS).getValue(Boolean.class))
                                                        ));
                                                        listener.onResult(1, list);
                                                    } else listener.onResult(0, null);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    listener.onFailure(error.getMessage());
                                                }
                                            });
                                } else listener.onResult(0, null);
                            }
                        } else listener.onResult(0, null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    public static void ignoredList(String senderID, OnDataResultListener listener) {
        List<User> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(BUCKET_FRIENDS)
                .child(senderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot user : snapshot.getChildren()) {
                                if (Objects.equals(user.getValue(String.class), STATUS_IGNORED)) {
                                    FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                                            .orderByChild(USER_VALUE_ID).equalTo(user.getKey())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        DataSnapshot user = snapshot.getChildren().iterator().next();
                                                        list.add(new User(
                                                                Objects.requireNonNull(user.child(USER_VALUE_ID).getValue(String.class)),
                                                                user.child(USER_VALUE_USERNAME).getValue(String.class),
                                                                user.child(USER_VALUE_NAME).getValue(String.class),
                                                                user.child(USER_VALUE_IMAGE).getValue(String.class),
                                                                Boolean.TRUE.equals(user.child(USER_VALUE_FRIENDS).getValue(Boolean.class))
                                                        ));
                                                        listener.onResult(1, list);
                                                    } else listener.onResult(0, null);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    listener.onFailure(error.getMessage());
                                                }
                                            });
                                } else listener.onResult(0, null);
                            }
                        } else listener.onResult(0, null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    public interface OnResultListener {
        void onResult(int result);

        void onFailure(String message);
    }

    public interface OnDataResultListener {
        void onResult(int resultCode, List<User> list);

        void onFailure(String message);
    }
}