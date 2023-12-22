package com.github.fearmygaze.mercury.firebase.dao;

import com.github.fearmygaze.mercury.firebase.RoomCallBackResponse;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.firestore.Query;

import java.util.List;

public interface ChatRoomDao {

    ///////////////////////////////////////////////////////////////////////////
    // Room
    ///////////////////////////////////////////////////////////////////////////

    Query getRooms(String userID);

    void create(Profile user, List<Profile> participants, Room.RoomType type, boolean encrypted, RoomCallBackResponse<Room> callBackResponse);

    void exists(Profile user, List<Profile> participants, boolean encrypted, RoomCallBackResponse<Room> callBackResponse);

    void updateParticipants(String roomID, List<Profile> participants, RoomCallBackResponse<Room> callBackResponse);

    void updateName(String roomID, String updatedName, RoomCallBackResponse<String> callBackResponse);

    void leave(String roomID, Profile profile, RoomCallBackResponse<String> callBackResponse);

    void delete(String roomID, RoomCallBackResponse<String> callBackResponse);

    void roomEventListener(String roomID, RoomCallBackResponse<Room> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Messages
    ///////////////////////////////////////////////////////////////////////////

    void sendTextMessage(String userID, String roomID, String text, RoomCallBackResponse<String> callBackResponse);

    void sendImageMessage(String userID, String roomID, String imageUrl, RoomCallBackResponse<String> callBackResponse);

    void sendSoundBite(String userID, String roomID, RoomCallBackResponse<String> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Reactions
    ///////////////////////////////////////////////////////////////////////////

    void addReaction(String msgID, String roomID, User user, RoomCallBackResponse<String> callBackResponse);

    void removeReaction(String msgID, String roomID, User user, RoomCallBackResponse<String> callBackResponse);

    //TODO: Add
    //      message sub-collection event listener
    //      send notification
}
