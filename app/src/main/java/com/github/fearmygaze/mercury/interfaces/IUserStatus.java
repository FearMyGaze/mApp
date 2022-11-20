package com.github.fearmygaze.mercury.interfaces;

public interface IUserStatus {
    void onSuccess();
    void onExit(String message);
    void onError(String message);
}