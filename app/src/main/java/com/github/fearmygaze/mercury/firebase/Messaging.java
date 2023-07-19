package com.github.fearmygaze.mercury.firebase;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Messaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            User localUser = AppDatabase.getInstance(getApplicationContext()).userDao().getUserByUserID(user.getUid());
            if (!localUser.getNotificationToken().equals(token)) {
                Auth.updateNotificationToken(user, token, getApplicationContext());
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}