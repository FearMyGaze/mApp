package com.github.fearmygaze.mercury.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.fearmygaze.mercury.model.Profile;

import java.util.List;

@Dao
public interface CProfileDao {

    @Query("SELECT * FROM cachedProfiles")
    List<Profile> getAll();

    @Query("DELETE FROM cachedProfiles")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Profile profile);

    @Delete
    void delete(Profile profile);

}
