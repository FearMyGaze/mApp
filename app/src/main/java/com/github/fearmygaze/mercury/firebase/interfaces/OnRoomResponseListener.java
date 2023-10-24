package com.github.fearmygaze.mercury.firebase.interfaces;

import com.github.fearmygaze.mercury.model.Room;

public interface OnRoomResponseListener {
    void onSuccess(int code, Room room);

    void onFailure(String msg);
}
