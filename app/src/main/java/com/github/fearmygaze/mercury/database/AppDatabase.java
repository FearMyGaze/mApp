package com.github.fearmygaze.mercury.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.github.fearmygaze.mercury.custom.TimestampConverter;
import com.github.fearmygaze.mercury.model.CachedProfile;
import com.github.fearmygaze.mercury.model.CachedQuery;
import com.github.fearmygaze.mercury.model.User;

@Database(entities = {User.class, CachedProfile.class, CachedQuery.class}, version = 1, exportSchema = false)
@TypeConverters({TimestampConverter.class}) //TODO: Change the version num
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
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
