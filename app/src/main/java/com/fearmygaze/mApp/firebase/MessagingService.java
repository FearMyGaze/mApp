package com.fearmygaze.mApp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {

        /*
         * TODO: sharedPreference the token and when the user login and after the login update the token
         * */
//        PrivatePreference preference = new PrivatePreference(getApplicationContext());
//
//        preference.putString("token",token);

        super.onNewToken(token);
        Log.d("FCM", "onNewToken: "+ token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM", "onMessageReceived: " + message.getNotification().getBody());
    }
}