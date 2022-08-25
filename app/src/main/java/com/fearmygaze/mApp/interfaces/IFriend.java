package com.fearmygaze.mApp.interfaces;

import com.fearmygaze.mApp.model.Friend;

import java.util.List;

public interface IFriend {
    void onSuccess(List<Friend> friendList);
    void onError(String message);
}