package com.fearmygaze.mApp.interfaces;

import com.fearmygaze.mApp.model.User1;

public interface IUserStatus {
    void onSuccess(User1 user);
    void onExit(String message);
    void onError(String message);
}