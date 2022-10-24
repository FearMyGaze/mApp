package com.fearmygaze.mApp.interfaces.forms;

public interface IFormSignUp {
    void onSuccess(String message);
    void onValidationError(String message);
    void onError(String message);
}