package com.github.fearmygaze.mercury.interfaces;

import com.github.fearmygaze.mercury.model.Friend;

import java.util.List;

public interface IFriend {
    void onSuccess(List<Friend> friendList);
    void onError(String message);
}