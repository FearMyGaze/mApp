package com.fearmygaze.mApp.interfaces;

import com.fearmygaze.mApp.model.FriendRequest;

import java.util.List;

public interface IFriendRequest {
    void onSuccess(List<FriendRequest> friendRequests);
    void onError(String message);
}