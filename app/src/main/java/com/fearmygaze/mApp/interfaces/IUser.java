package com.fearmygaze.mApp.interfaces;

import com.fearmygaze.mApp.model.User;

public interface IUser {
    void onSuccess(User user, String message);
    void onError(String message);
}