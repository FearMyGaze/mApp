package com.github.fearmygaze.mercury.firebase.interfaces;

public interface IFireCallback<T> {
    void onSuccess(T result);

    void onError(String message);

}
