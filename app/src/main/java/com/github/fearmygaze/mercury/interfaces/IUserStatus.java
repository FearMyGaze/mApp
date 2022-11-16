package com.github.fearmygaze.mercury.interfaces;

import com.github.fearmygaze.mercury.model.User;

public interface IUserStatus {
    void onSuccess(User user);
    void onExit(String message);
    void onError(String message);
}