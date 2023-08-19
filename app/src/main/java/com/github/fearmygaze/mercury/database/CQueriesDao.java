package com.github.fearmygaze.mercury.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.fearmygaze.mercury.model.CachedQuery;

import java.util.List;

@Dao
public interface CQueriesDao {

    @Query("SELECT * FROM cachedQueries")
    List<CachedQuery>getAll();

    @Query("DELETE FROM cachedQueries")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CachedQuery query);

    @Delete
    void delete(CachedQuery query);
}
