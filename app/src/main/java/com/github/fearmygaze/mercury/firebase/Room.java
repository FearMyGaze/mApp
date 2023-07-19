package com.github.fearmygaze.mercury.firebase;

import com.github.fearmygaze.mercury.model.RoomData;
import com.github.fearmygaze.mercury.model.User;

import java.util.List;
import java.util.Map;

public class Room {

    /*
     * TODO:
     *       We need to separate the storing of the Rooms to minimize the searching
     *       possibly the same way like Friends
     *
     * */


    //FIXME: OLD

    private static final String ROOM_BUCKET = "rooms";
    private static final String ROOM_ID = "id";
    private static final String ROOM_TYPE = "type";
    private static final String ROOM_TYPE_SOLO = "solo";
    private static final String ROOM_TYPE_GROUP = "group";
    private static final String ROOM_CREATOR_ID = "creatorID";
    private static final String ROOM_MEMBERS = "members";
    private static final String ROOM_NAME = "name";

    private static final String USER_BUCKET = "users";
    private static final String USER_ID = "userUID";
    private static final String USER_USERNAME = "username";

    public static void Exists(User user, List<String> members, OnDataResultListener listener) {
        if (members.size() == 1) {
//            FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                    .orderByChild(ROOM_CREATOR_ID).equalTo(user.userUID)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                boolean found = false;
//                                for (DataSnapshot ds : snapshot.getChildren()) {
//                                    if (Objects.equals(ds.child(ROOM_TYPE).getValue(String.class), ROOM_TYPE_SOLO)) {
//                                        DataSnapshot mDS = ds.child(ROOM_MEMBERS);
//                                        if (Objects.equals(mDS.getChildren().iterator().next().getValue(String.class), members.get(0))) {
//                                            found = true;
//                                        }
//                                    }
//                                }
//                                if (!found) {
//                                    Room.Create(user, members, listener);
//                                } else listener.onResult(2, null);
//                            } else Room.Create(user, members, listener);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            listener.onFailure(error.getMessage());
//                        }
//                    });
        } else Room.Create(user, members, listener);
    }

    public static void PrepareRoom(User user, Map<String, Boolean> members, OnDataResultListener listener) {
//        DatabaseReference chatRoomsRef = FirebaseDatabase.getInstance().getReference(ROOM_BUCKET);
//        String roomID = chatRoomsRef.push().getKey();
//        if (roomID != null) {
//            if (members.size() == 1) {
//                FirebaseDatabase.getInstance().getReference(ROOM_BUCKET)
//                        .orderByChild(ROOM_MEMBERS + "/" + user.userUID).equalTo(true)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()) {
//                                    boolean found = false;
//                                    String id;
//                                    for (DataSnapshot dS : snapshot.getChildren()) {
//                                        if (Objects.equals(dS.child(ROOM_MEMBERS).getChildren().iterator().next().getKey(), members.keySet().iterator().next())) {
//                                            found = true;
//                                            id = dS.child(ROOM_ID).getValue(String.class);
//                                            Log.d("customLog", id + "");
//                                        }
//                                        Log.d("customLog", String.valueOf(dS.getValue()));
//                                    }
//                                    if (!found) {
//                                        listener.onResult(1, roomID);
//                                    } else listener.onResult(2, null);
//                                } else listener.onResult(0, roomID);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                listener.onFailure(error.getMessage());
//                            }
//                        });
//            }
//        }
    }

    public static void ExistsNew(User user, Map<String, Boolean> members, OnResultListener listener) {
//        if (members.size() == 1) {
//            FirebaseDatabase.getInstance().getReference(ROOM_BUCKET)
//                    .orderByChild(ROOM_MEMBERS + "/" + user.userUID).equalTo(true)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                boolean found = false;
//                                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
//                                    found = true;
//                                    String roomId = roomSnapshot.getKey();
//                                    String roomTitle = roomSnapshot.child(ROOM_NAME).getValue(String.class);
//                                    // Access the chat room data
//                                    // Do something with the room ID and title
//                                    // ...
//                                    Log.d("customLog", roomId + "  " + roomTitle);
//                                }
//                                if (!found) {
//                                    Room.Create(user, members, listener);
//                                } else listener.onResult(2);
//                            } else Room.Create(user, members, listener);
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            listener.onFailure(error.getMessage());
//                        }
//                    });
//        } else {
//            Room.Create(user, members, listener);
//        }
    }

    public static void GetRooms(User user, OnDataResultListener listener) {
//        List<RoomData> roomData = new ArrayList<>();
//        FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                .orderByChild(ROOM_MEMBERS).equalTo(user.userUID)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            for (DataSnapshot ds : snapshot.getChildren()) {
//                                roomData.add(ds.getValue(RoomData.class));
//                            }
//                            listener.onResult(1, roomData);
//                        } else listener.onResult(-1, null);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        listener.onFailure(error.getMessage());
//                    }
//                });
    }

    public static void Create(User user, Map<String, Boolean> members, OnResultListener listener) {
//        DatabaseReference chatRoomsRef = FirebaseDatabase.getInstance().getReference(ROOM_BUCKET);
//        String roomID = chatRoomsRef.push().getKey();
//        if (members.size() == 1) {
//            Room.GetUser(members.keySet().iterator().next(), new OnDataResultListener() {
//                @Override
//                public void onResult(int resultCode, Object object) {
//                    if (resultCode == 1 && roomID != null) {
//                        chatRoomsRef.child(roomID)
//                                .setValue(new RoomData(
//                                        roomID,
//                                        ROOM_TYPE_SOLO,
//                                        user.username + " ," + object.toString(),
//                                        user.userUID,
//                                        members)
//                                        .toMap()
//                                )
//                                .addOnSuccessListener(unused -> listener.onResult(1))
//                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                    } else listener.onResult(-1);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    listener.onFailure(message);
//                }
//            });
//        } else {
//            Room.GetUser(members.keySet().iterator().next(), new OnDataResultListener() {
//                @Override
//                public void onResult(int resultCode, Object object) {
//                    if (resultCode == 1 && roomID != null) {
//                        chatRoomsRef.child(roomID)
//                                .setValue(new RoomData(
//                                        roomID,
//                                        ROOM_TYPE_GROUP,
//                                        user.userUID + ", " + object.toString() + " + " + (members.size() - 1),
//                                        user.userUID,
//                                        members)
//                                        .toMap()
//                                )
//                                .addOnSuccessListener(unused -> listener.onResult(1))
//                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                    } else listener.onResult(-1);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    listener.onFailure(message);
//                }
//            });
//        }

    }

    public static void Create(User user, List<String> members, OnDataResultListener listener) {
//        DatabaseReference chatRoomsRef = FirebaseDatabase.getInstance().getReference(ROOM_BUCKET);
//        String roomID = chatRoomsRef.push().getKey();
//        if (members.size() == 1) {
//            Room.GetUser(members.get(0), new OnDataResultListener() {
//                @Override
//                public void onResult(int resultCode, Object object) {
//                    if (resultCode == 1 && roomID != null) {
//                        members.add(user.userUID);
//                        chatRoomsRef.child(roomID)
//                                .setValue(new RoomData(
//                                        roomID,
//                                        ROOM_TYPE_SOLO,
//                                        user.username + " ," + object.toString(),
//                                        user.userUID,
//                                        members).toMap())
//                                .addOnSuccessListener(unused -> listener.onResult(1, roomID))
//                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                    } else listener.onResult(-1, null);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    listener.onFailure(message);
//                }
//            });
//        } else {
//            Room.GetUser(members.get(0), new OnDataResultListener() {
//                @Override
//                public void onResult(int resultCode, Object object) {
//                    if (resultCode == 1 && roomID != null) {
//                        members.add(user.userUID);
//                        chatRoomsRef.child(roomID)
//                                .setValue(new RoomData(roomID, ROOM_TYPE_GROUP,
//                                        user.userUID + ", " + object.toString() + " + " + (members.size() - 1),
//                                        user.userUID,
//                                        members).toMap())
//                                .addOnSuccessListener(unused -> listener.onResult(1, roomID))
//                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                    } else listener.onResult(-1, null);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    listener.onFailure(message);
//                }
//            });
//        }
    }

    public static void ChangeName(RoomData roomData, String roomName, OnResultListener listener) {
//        FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                .child(ROOM_ID).equalTo(roomData.id).limitToFirst(1)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                                    .setValue(new RoomData(roomData.id,
//                                            roomData.type,
//                                            roomName,
//                                            roomData.creatorID,
//                                            roomData.members)
//                                            .toMap())
//                                    .addOnSuccessListener(unused -> listener.onResult(1))
//                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                        } else listener.onResult(-1);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        listener.onFailure(error.getMessage());
//                    }
//                });
    }

    public static void UpdateUsers(RoomData roomData, List<String> members, OnResultListener listener) {
//        FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                .child(ROOM_ID).equalTo(roomData.id).limitToFirst(1)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            if (members.size() == 1) {
//                                FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                                        .child(roomData.id).setValue(new RoomData(roomData.id,
//                                                ROOM_TYPE_SOLO,
//                                                roomData.name,
//                                                roomData.creatorID,
//                                                members)
//                                                .toMap())
//                                        .addOnSuccessListener(unused -> listener.onResult(1))
//                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                            } else {//This is for when the users goes from 1 to more to change the Type
//                                FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                                        .child(roomData.id).setValue(new RoomData(roomData.id,
//                                                ROOM_TYPE_GROUP,
//                                                roomData.name,
//                                                roomData.creatorID,
//                                                members)
//                                                .toMap())
//                                        .addOnSuccessListener(unused -> listener.onResult(1))
//                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                            }
//                        } else listener.onResult(-1);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        listener.onFailure(error.getMessage());
//                    }
//                });
    }

    public static void Delete(String roomID, OnResultListener listener) {
//        FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                .child(ROOM_ID).equalTo(roomID).limitToFirst(1)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            FirebaseDatabase.getInstance().getReference().child(ROOM_BUCKET)
//                                    .child(ROOM_ID).removeValue()
//                                    .addOnSuccessListener(unused -> listener.onResult(1))
//                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
//                        } else listener.onResult(-1);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        listener.onFailure(error.getMessage());
//                    }
//                });
    }

    private static void GetUser(String userID, OnDataResultListener listener) {
//        FirebaseDatabase.getInstance().getReference().child(USER_BUCKET)
//                .orderByChild(USER_ID).equalTo(userID).limitToFirst(1)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            listener.onResult(1, snapshot.getChildren().iterator().next().child(USER_USERNAME).getValue(String.class));
//                        } else listener.onResult(-1, null);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        listener.onFailure(error.getMessage());
//                    }
//                });
    }

    public interface OnResultListener {
        void onResult(int resultCode);

        void onFailure(String message);
    }

    public interface OnDataResultListener {
        void onResult(int resultCode, Object object);

        void onFailure(String message);
    }
}