package com.github.fearmygaze.mercury.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.fearmygaze.mercury.database.model.PrevSearch;

import java.util.List;

@Dao
public interface PrevSearchDao {

    @Query("SELECT * from searches")
    List<PrevSearch> getAll();

    @Query("SELECT * from searches WHERE byUserId = :userId")
    List<PrevSearch> getAllFor(String userId);

    @Query("DELETE FROM searches")
    void deleteAll();

    @Query("DELETE FROM searches WHERE byUserId = :userId")
    void deleteAllFor(String userId);

    @Insert
    void insert(PrevSearch search);

    @Delete
    void delete(PrevSearch search);

    default void delete(String userId) {
        if (userId == null || userId.isEmpty()) {
            deleteAll();
        } else {
            deleteAllFor(userId);
        }
    }

}
