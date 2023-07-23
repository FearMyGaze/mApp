package com.github.fearmygaze.mercury.firebase.interfaces;

import com.github.fearmygaze.mercury.model.Room;

public interface OnRoomDataListener {
    void onSuccess(int code, Room data);

    void onFailure(String message);
}
