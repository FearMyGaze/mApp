package com.github.fearmygaze.mercury.firebase.interfaces;

import com.github.fearmygaze.mercury.model.User;

import java.util.List;

public interface OnUsersResponseListener {
    void onSuccess(int code, List<User> list);

    void onFailure(String message);
}
