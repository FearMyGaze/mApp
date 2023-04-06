package com.github.fearmygaze.mercury.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.fearmygaze.mercury.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE userUID = :str")
    User getUserByUserUID(String str);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}