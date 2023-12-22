package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.github.fearmygaze.mercury.firebase.dao.RoomActionsDao;
import com.github.fearmygaze.mercury.model.Message;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RoomActions implements RoomActionsDao {
    private final FirebaseFirestore database;
    private final Context ctx;

    public RoomActions(Context context) {
        this.database = FirebaseFirestore.getInstance();
        this.ctx = context;
    }

    @Override
    public Query getRooms(String userID) {
        return database.collection("chatRooms")
                .whereArrayContains("visibleTo", userID)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    /**
     * @param user             The small profile of the current user is logged in
     * @param participants     The list of participants belonging to the room
     * @param type             A flag that states the type of the room
     * @param encrypted        A flag that notifies the device to search for the local encryption key and such
     * @param callBackResponse A callback that returns the Room object if the action is successful or a String if an error occurs
     */
    @Override
    public void create(Profile user, List<Profile> participants, Room.RoomType type, boolean encrypted, CallBackResponse<Room> callBackResponse) {
        DocumentReference reference = database.collection("chatRooms").document();
        Room _room = new Room(reference.getId(),
                Room.createName(user.getUsername(), participants),
                user.getId(),
                type,
                encrypted,
                Room.addVisibleTo(user.getId(), participants),
                Room.addProfiles(user, participants),
                null);
        reference.set(_room)
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to create the room"))
                .addOnFailureListener(e -> Log.d("customLog", e.getMessage()))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess(_room));
    }

    /**
     * @param user             The small profile of the current user is logged in
     * @param participants     The list of participants belonging to the room
     * @param encrypted        A flag that notifies the device to search for the local encryption key and such
     * @param callBackResponse A callback that return the room object if the action is successful or a string if an error occurs
     */
    @Override
    public void exists(Profile user, List<Profile> participants, boolean encrypted, CallBackResponse<Room> callBackResponse) {
        Room.RoomType type = participants.size() > 1 ? Room.RoomType.Group : Room.RoomType.Private;
        if (type == Room.RoomType.Private) {
            database.collection("chatRooms")
                    .whereEqualTo("roomValidation", user.getUsername() + "_" + participants.get(0).getUsername())
                    .whereEqualTo("roomType", Room.RoomType.Private)
                    .orderBy("created", Query.Direction.DESCENDING)
                    .get()
                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to search for pre-existing rooms"))
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            Room room = queryDocumentSnapshots.getDocuments().get(0).toObject(Room.class);
                            if (room != null) {
                                callBackResponse.onSuccess(room);
                            } else {
                                callBackResponse.onError("Error getting the room");
                            }
                        } else {
                            create(user, participants, type, encrypted, callBackResponse);
                        }
                    });
        } else {
            create(user, participants, type, encrypted, callBackResponse);
        }
    }

    @Override
    public void roomEventListener(String roomID, CallBackResponse<Room> callBackResponse) {
        database.collection("chatRooms")
                .document(roomID)
                .addSnapshotListener((doc, err) -> {
                    if (err != null) {
                        callBackResponse.onFailure("Failed to get the room");
                    }

                    if (doc != null && doc.exists()) {
                        callBackResponse.onSuccess(doc.toObject(Room.class));
                    } else callBackResponse.onError("Room is empty");
                });
    }

    @Override
    public void updateParticipants(String roomID, List<Profile> participants, CallBackResponse<Room> callBackResponse) {

    }

    /**
     * @param roomID           The specific id for the room
     * @param updatedName      The new name you want to set for the room
     * @param callBackResponse A callback that returns a message based on what happened
     */
    @Override
    public void updateName(String roomID, String updatedName, CallBackResponse<String> callBackResponse) {
        database.collection("chatRooms")
                .document(roomID)
                .update("roomName", updatedName, "nameModified", true)
                .addOnFailureListener(e -> callBackResponse.onFailure("To update the name you have be the owner of this room"))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess("Name updated successfully"));
    }

    /**
     * @param userID           The id of the current logged in user
     * @param roomID           The specific id of the room
     * @param text             The text we want to send
     * @param callBackResponse A callback that returns a message based on what happened
     */
    @Override
    public void sendTextMessage(String userID, String roomID, String text, CallBackResponse<String> callBackResponse) {
        DocumentReference reference = database
                .collection("chatRooms")
                .document(roomID)
                .collection("messages")
                .document();

        Message message = new Message(reference.getId(), roomID, userID, text, Message.MsgType.TXT, null);

        reference.set(message)
                .addOnFailureListener(e -> callBackResponse.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess(""))
                .addOnSuccessListener(unused -> {/*TODO: now we need to update the roomMessage Collection*/});
    }

    @Override
    public void sendImageMessage(String userID, String roomID, String imageUrl, CallBackResponse<String> callBackResponse) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(User.IMAGE_COLLECTION);
        StorageReference imageRef = storageRef.child(FireStorage.generateFileName(Uri.parse(imageUrl), ctx));
        imageRef.putFile(Uri.parse(imageUrl))
                .addOnFailureListener(e -> callBackResponse.onFailure(e.getMessage()))
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnFailureListener(e -> callBackResponse.onFailure(e.getMessage()))
                        .addOnSuccessListener(url -> {

                        }));

        /*
         * TODO:
         *   We need to find the correct naming for the file
         *   1st: we need to upload the image
         *   2nd: we need to get the link
         *   3rd: send the message
         * */
    }

    @Override
    public void sendSoundBite(String userID, String roomID, CallBackResponse<String> callBackResponse) {
        /*
         * TODO:
         *   1st: we need to save the file to the device
         *   2nd: we need to upload the sound
         *   3rd: we need to get the link
         *   4th: send the message
         * */
    }

    @Override
    public void addReaction(String msgID, String roomID, User user, CallBackResponse<String> callBackResponse) {
        DocumentReference reference = database
                .collection("chatRooms")
                .document(roomID)
                .collection("messages")
                .document(msgID);
        reference.get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the message"))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Message message = documentSnapshot.toObject(Message.class);
                        if (message != null) {
                            List<Profile> fetchedLikes = message.getLikes();
                            fetchedLikes.add(new Profile(user.getId(), user.getUsername(), user.getImage()));
                            reference.update("likes", fetchedLikes);
                        }
                    } else callBackResponse.onError("Error parsing the message");
                });
    }

    @Override
    public void removeReaction(String msgID, String roomID, User user, CallBackResponse<String> callBackResponse) {
        DocumentReference reference = database
                .collection("chatRooms")
                .document(roomID)
                .collection("messages")
                .document(msgID);
        reference.get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the message"))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Message message = documentSnapshot.toObject(Message.class);
                        if (message != null) {
                            List<Profile> fetchedLikes = message.getLikes();
                            fetchedLikes.remove(new Profile(user.getId(), user.getUsername(), user.getImage()));
                            reference.update("likes", fetchedLikes);
                        }
                    } else callBackResponse.onError("Error parsing the message");
                });
    }

    /**
     * @param roomID           The specific id for the room
     * @param userProfile      The profile of the current logged in user
     * @param callBackResponse A callback that returns a string based on what happened
     */
    @Override
    public void leave(String roomID, Profile userProfile, CallBackResponse<String> callBackResponse) {
        database.collection("chatRooms")
                .document(roomID)
                .get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the room either you are not in the room or there is a problem in our end"))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Room _room = documentSnapshot.toObject(Room.class);
                        //TODO: we need to add a check that will stop the user if the room is
                        // Private or has only 2 people
                        if (_room != null) {
                            _room.getVisibleTo().remove(userProfile.getId());
                            _room.getProfiles().remove(userProfile);
                            documentSnapshot.getReference()
                                    .update("visibleTo", _room.getVisibleTo(),
                                            "profiles", _room.getProfiles())
                                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to leave the room"))
                                    .addOnSuccessListener(unused -> callBackResponse.onSuccess("Successfully left the room"));
                        } else {
                            callBackResponse.onError("Failed to parse the room");
                        }
                    } else {
                        callBackResponse.onError("Failed to parse the room");
                    }
                });
    }

    /**
     * @param roomID           The specific id for the room
     * @param callBackResponse A callback that returns a string based on what happened
     */
    @Override
    public void delete(String roomID, CallBackResponse<String> callBackResponse) {
        database.collection("chatRooms")
                .document(roomID)
                .delete()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to delete the room either you are not the owner or there is a problem in our end"))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess("Successfully deleted the room"));
    }
}
