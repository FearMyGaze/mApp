package com.github.fearmygaze.mercury.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.fearmygaze.mercury.database.dao.BlockedUserDao;
import com.github.fearmygaze.mercury.database.dao.PrevSearchDao;
import com.github.fearmygaze.mercury.database.dao.UserDao;
import com.github.fearmygaze.mercury.database.dao.VisitedProfileDao;
import com.github.fearmygaze.mercury.database.model.BlockedUser;
import com.github.fearmygaze.mercury.database.model.PrevSearch;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.database.model.VisitedProfile;

@Database(entities = {
        User1.class, VisitedProfile.class,
        PrevSearch.class, BlockedUser.class},
        version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase {

    public abstract UserDao users();

    public abstract VisitedProfileDao profiles();

    public abstract PrevSearchDao searches();

    public abstract BlockedUserDao blocked();

    private static RoomDB INSTANCE;

    public static RoomDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), RoomDB.class, "mercury_DB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() //TODO: Remove this when the migration code is enabled
//                    .addMigrations(MIGRATION)
                    .build();
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase sqLite) {
            sqLite.execSQL("BEGIN TRANSACTION");
            /*
             * TODO:
             *      Step 1: Create the tables if they don't exist with the primary keys
             *      Step 2: Set the values from the old table to the new
             *      Step 3: Delete the old tables
             *      Step 4: Update the name of the tables with the old names
             * */
            sqLite.execSQL("COMMIT");
        }
    };
}
