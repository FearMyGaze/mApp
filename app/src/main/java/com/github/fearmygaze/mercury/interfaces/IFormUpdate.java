package com.github.fearmygaze.mercury.interfaces;

public interface IFormUpdate {
    void onSuccess(String message);
    void onValidationError(String message);
    void onError(String message);
}