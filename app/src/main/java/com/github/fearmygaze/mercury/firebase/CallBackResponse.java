package com.github.fearmygaze.mercury.firebase;

public interface CallBackResponse<T> {
    void onSuccess(T object);

    void onError(String message);

    void onFailure(String message);
}
