package com.github.fearmygaze.mercury.firebase;

import android.net.Uri;

import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;

public interface StorageDao {
    void uploadProfileImage(Uri image, CallBackResponse<String> response);
}
