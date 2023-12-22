package com.github.fearmygaze.mercury.firebase.interfaces;

import com.github.fearmygaze.mercury.model.Request;

public interface OnRequestResponseListener {
    void onSuccess(int code, Request request);
    void onFailure(String message);
}
