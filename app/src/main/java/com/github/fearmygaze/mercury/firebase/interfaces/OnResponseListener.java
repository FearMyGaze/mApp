package com.github.fearmygaze.mercury.firebase.interfaces;

public interface OnResponseListener {
    void onSuccess(int code);

    void onFailure(String message);
}
