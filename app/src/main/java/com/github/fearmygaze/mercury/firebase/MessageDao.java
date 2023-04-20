package com.github.fearmygaze.mercury.firebase;

import com.github.fearmygaze.mercury.model.Message;
import com.google.firebase.database.DatabaseReference;

public class MessageDao {
    DatabaseReference reference;

    public MessageDao(DatabaseReference databaseReference) {
        this.reference = databaseReference;
    }

    public void create(Message message) {

    }

    public void update(Message message, String[] params) {

    }

    public void delete(Message message) {

    }

}
