package com.fearmygaze.mApp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fearmygaze.mApp.dao.UserDao;
import com.fearmygaze.mApp.model.User1;

@Database(entities = {User1.class}, version = 2, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    private static UserDatabase INSTANCE;

    public static UserDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.
                            databaseBuilder(context.getApplicationContext(), UserDatabase.class, "user_data_db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }
}