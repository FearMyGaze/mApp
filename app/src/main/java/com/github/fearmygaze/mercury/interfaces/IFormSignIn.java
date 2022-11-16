package com.github.fearmygaze.mercury.interfaces;

public interface IFormSignIn {
    void onSuccess(int id, String message);
    void onValidationError(String message);
    void onError(String message);
}