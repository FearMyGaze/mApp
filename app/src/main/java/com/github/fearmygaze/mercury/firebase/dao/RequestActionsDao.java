package com.github.fearmygaze.mercury.firebase.dao;

import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.google.firebase.firestore.Query;

public interface RequestActionsDao {

    Query waiting(String myUserID);

    Query friends(String myUserID);

    Query blocked(String myUserID);

    void eventListener(String myUserID, String otherUserID, CallBackResponse<Request> callBackResponse);

    void get(String requestID, CallBackResponse<Request> callBackResponse);

    void create(Profile myUser, Profile otherUser, CallBackResponse<String> callBackResponse);

    void accept(String requestID, CallBackResponse<String> callBackResponse);

    void deny(String requestID, CallBackResponse<String> callBackResponse);

    void block(Profile myUser, Profile otherUser, CallBackResponse<String> callBackResponse);

    void unBlock(String requestID, CallBackResponse<String> callBackResponse);

    void delete(String requestID, CallBackResponse<String> callBackResponse);

    void report(String myUserID, Profile otherUser, CallBackResponse<String> callBackResponse);

}
