package com.github.fearmygaze.mercury.firebase.dao;

import android.net.Uri;

import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;

public interface StorageActionsDao {

    void uploadFile(Uri file, String fileName, String location, CallBackResponse<String> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Naming
    ///////////////////////////////////////////////////////////////////////////

    String generateProfileImageName(Uri uri);

}
