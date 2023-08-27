package com.github.fearmygaze.mercury.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.github.fearmygaze.mercury.custom.MercuryDBCallback;
import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.CachedQuery;
import com.github.fearmygaze.mercury.model.User;

@Database(entities = {User.class, Profile.class, CachedQuery.class}, version = 1, exportSchema = false)
@TypeConverters({TimestampConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserRoomDao userDao();

    public abstract CProfileDao cachedProfile();

    public abstract CQueriesDao cachedQueries();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mercury_DB")
                    .allowMainThreadQueries()
                    .addCallback(new MercuryDBCallback(context.getApplicationContext()))
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
