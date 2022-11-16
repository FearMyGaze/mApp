package com.github.fearmygaze.mercury.firebase;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        PrivatePreference preference = new PrivatePreference(getApplicationContext());
        preference.putString("token", token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}