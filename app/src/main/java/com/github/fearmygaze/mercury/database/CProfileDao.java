package com.github.fearmygaze.mercury.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.fearmygaze.mercury.model.CachedProfile;

import java.util.List;

@Dao
public interface CProfileDao {

    @Query("SELECT * FROM cachedProfiles")
    List<CachedProfile> getAll();

    @Query("DELETE FROM cachedProfiles")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CachedProfile profile);

    @Delete
    void delete(CachedProfile profile);

}
