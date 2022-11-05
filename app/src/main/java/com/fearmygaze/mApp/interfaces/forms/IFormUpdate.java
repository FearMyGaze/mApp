package com.fearmygaze.mApp.interfaces.forms;

public interface IFormUpdate {
    void onSuccess(String message);
    void onValidationError(String message);
    void onError(String message);
}