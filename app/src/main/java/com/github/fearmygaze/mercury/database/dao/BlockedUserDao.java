package com.github.fearmygaze.mercury.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.fearmygaze.mercury.database.model.BlockedUser;

import java.util.List;

@Dao
public interface BlockedUserDao {

    @Query("SELECT * FROM blocked WHERE byUserId = :byUserId")
    List<BlockedUser> getAll(String byUserId);

    @Query("DELETE FROM blocked WHERE id = :byUserId")
    void deleteAll(String byUserId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlockedUser blockedUser);

    @Delete
    void delete(BlockedUser blockedUser);
}
