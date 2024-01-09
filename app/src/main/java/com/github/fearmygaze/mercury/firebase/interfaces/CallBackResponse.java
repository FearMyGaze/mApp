package com.github.fearmygaze.mercury.firebase.interfaces;

public interface CallBackResponse<T> {
    void onSuccess(T object);

    void onError(String message);

    void onFailure(String message);
}
