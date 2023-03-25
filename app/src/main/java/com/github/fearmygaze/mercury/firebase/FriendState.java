package com.github.fearmygaze.mercury.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FriendState {

    public static void areTheyFriends(String userID, String otherUserID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child("friendRequests")
                .orderByChild("between").equalTo(betweenParser(userID, otherUserID)).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            FirebaseDatabase.getInstance().getReference().child("friendRequests")
                                    .orderByChild("between").equalTo(betweenParser(otherUserID, userID)).limitToFirst(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            listener.onResult(snapshot.exists());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            listener.onFailure(error.getMessage());
                                        }
                                    });
                        }else listener.onResult(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    public static void sendRequest(String user1ID, String user2ID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child("friendRequests")
                .push().setValue(requestToMap(user1ID, user2ID))
                .addOnSuccessListener(unused -> listener.onResult(true))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));

    }

    public static void removeRequest(String user1ID, String user2ID, OnResultListener listener) {
        FirebaseDatabase.getInstance().getReference().child("friendRequests")
                .orderByChild("between").equalTo(betweenParser(user1ID, user2ID)).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            snapshot.getRef().removeValue()
                                    .addOnSuccessListener(unused -> listener.onResult(true))
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("friendRequests")
                                    .orderByChild("between").equalTo(betweenParser(user2ID, user1ID)).limitToFirst(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            if (dataSnapshot1.exists()) {
                                                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                                                snapshot.getRef().removeValue()
                                                        .addOnSuccessListener(unused -> listener.onResult(true))
                                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                            }else listener.onResult(false);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            listener.onFailure(error.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });


    }

    public static void showFriendsBasedOnState(String userID, String state, OnResultListener listener) {

    }

    public interface OnResultListener {
        void onResult(boolean result);

        void onFailure(String message);
    }

    private static String betweenParser(String input1, String input2) {
        return input1 + "-" + input2;
    }

    private static Map<String, Object> requestToMap(String user1_ID, String user2_ID) {
        Map<String, Object> map = new HashMap<>();
        map.put("between", user1_ID + "-" + user2_ID);
        map.put("state", "pending");
        return map;
    }
}
