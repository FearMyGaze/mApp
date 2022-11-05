package com.fearmygaze.mApp.interfaces.forms;

public interface IFormSignIn {
    void onSuccess(int id, String message);
    void onValidationError(String message);
    void onError(String message);
}