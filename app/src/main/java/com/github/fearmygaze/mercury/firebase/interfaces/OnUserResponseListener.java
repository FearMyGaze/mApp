package com.github.fearmygaze.mercury.firebase.interfaces;

import com.github.fearmygaze.mercury.model.User;

public interface OnUserResponseListener {
    void onSuccess(int code, User user);

    void onFailure(String message);
}
