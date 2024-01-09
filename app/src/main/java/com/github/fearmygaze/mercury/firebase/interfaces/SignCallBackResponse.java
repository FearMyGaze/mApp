package com.github.fearmygaze.mercury.firebase.interfaces;

public interface SignCallBackResponse<T> {
    void onSuccess(T object);

    void onError(int error, String message);

    void onFailure(String message);
}
