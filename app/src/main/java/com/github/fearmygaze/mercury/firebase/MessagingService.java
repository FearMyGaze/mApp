package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.database.RoomDB;
import com.github.fearmygaze.mercury.database.model.User1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Context context = getApplicationContext();
            User1 local = RoomDB.getInstance(context)
                    .users().getByID(user.getUid());
            if (!local.getNotificationToken().equals(token)) {
                RoomDB.getInstance(context).users()
                        .transactionUpdateToken(token, local.getId());
                new Auth(context).updateMessagingToken(local.getId(), token);
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}
