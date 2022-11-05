package com.fearmygaze.mApp.interfaces;

import com.fearmygaze.mApp.model.User;

public interface IUserStatus {
    void onSuccess(User user);
    void onExit(String message);
    void onError(String message);
}