package com.github.fearmygaze.mercury.firebase;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Messaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            AppDatabase.getInstance(getApplicationContext()).userDao().updateUserToken(token, currentUser.getUid());
            User user = AppDatabase.getInstance(getApplicationContext()).userDao().getUserByUserUID(currentUser.getUid());
            FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).setValue(user.toMap(false));
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}