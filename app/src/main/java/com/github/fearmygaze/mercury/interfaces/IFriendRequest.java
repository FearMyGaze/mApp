package com.github.fearmygaze.mercury.interfaces;

import com.github.fearmygaze.mercury.model.FriendRequest;

import java.util.List;

public interface IFriendRequest {
    void onSuccess(List<FriendRequest> friendRequests);
    void onError(String message);
}