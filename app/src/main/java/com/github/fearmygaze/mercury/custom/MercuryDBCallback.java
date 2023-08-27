package com.github.fearmygaze.mercury.custom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.fearmygaze.mercury.database.AppDatabase;

public class MercuryDBCallback extends RoomDatabase.Callback {

    private final Context CONTEXT;

    public MercuryDBCallback(Context context) {
        CONTEXT = context.getApplicationContext();
    }

    @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isDatabaseIntegrityOk()) {
            CONTEXT.deleteDatabase("mercury_DB");
            Room.databaseBuilder(CONTEXT, AppDatabase.class, "mercury_DB").build();
        }
    }
}
