package com.fearmygaze.mApp.interfaces.forms;

import com.fearmygaze.mApp.model.User;

public interface IFormUpdate {
    void onSuccess(User user, String message);
    void onValidationError(String message);
    void onError(String message);
}