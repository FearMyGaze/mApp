package com.github.fearmygaze.mercury.firebase.dao;

import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.Query;

import java.util.List;

public interface RoomActionsDao {

    ///////////////////////////////////////////////////////////////////////////
    // Room
    ///////////////////////////////////////////////////////////////////////////

    Query getRooms(String userID);

    void create(Profile user, List<Profile> participants, Room.RoomType type, boolean encrypted, CallBackResponse<Room> callBackResponse);

    void exists(Profile user, List<Profile> participants, boolean encrypted, CallBackResponse<Room> callBackResponse);

    void updateParticipants(String roomID, Profile owner, List<Profile> participants, CallBackResponse<String> callBackResponse);

    void updateName(String roomID, String updatedName, CallBackResponse<String> callBackResponse);

    void leave(String roomID, Profile profile, CallBackResponse<String> callBackResponse);

    void delete(String roomID, CallBackResponse<String> callBackResponse);

    void roomEventListener(String roomID, CallBackResponse<Room> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Messages
    ///////////////////////////////////////////////////////////////////////////

    void sendTextMessage(String userID, String roomID, String text, CallBackResponse<String> callBackResponse);

    void sendImageMessage(String userID, String roomID, String imageUrl, CallBackResponse<String> callBackResponse);

    void sendSoundBite(String userID, String roomID, CallBackResponse<String> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Reactions
    ///////////////////////////////////////////////////////////////////////////

    void addReaction(String msgID, String roomID, User user, CallBackResponse<String> callBackResponse);

    void removeReaction(String msgID, String roomID, User user, CallBackResponse<String> callBackResponse);

    //TODO: Add
    //      message sub-collection event listener
    //      send notification
}
