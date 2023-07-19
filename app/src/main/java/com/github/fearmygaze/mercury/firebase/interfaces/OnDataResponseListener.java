package com.github.fearmygaze.mercury.firebase.interfaces;

public interface OnDataResponseListener {
    void onSuccess(int code, Object data);

    void onFailure(String message);
}
