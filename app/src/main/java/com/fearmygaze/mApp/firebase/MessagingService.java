package com.fearmygaze.mApp.firebase;

import androidx.annotation.NonNull;

import com.fearmygaze.mApp.util.PrivatePreference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        PrivatePreference preference = new PrivatePreference(getApplicationContext());
        preference.putString("token",token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}