package com.github.fearmygaze.mercury.firebase;

public class Room {

    public static void Exists() {

    }

    public static void Create() {

    }

    public static void Update() {

    }

    public static void Delete() {

    }

    public interface OnResultListener {
        void onResult(int resultCode);

        void onFailure(String message);
    }

    public interface onExtenderResultListener {
        void onResult(int resultCode, Object object);

        void onFailure(String message);
    }
}