package com.fearmygaze.mApp.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fearmygaze.mApp.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserByID(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT imageUrl FROM users WHERE id = :id")
    String getImageUrl(int id);

    @Query("UPDATE users SET imageUrl= :url WHERE id = :id")
    void updateImageByID(String url, int id);

    @Query("DELETE FROM users WHERE id= :id")
    void deleteUserByID(int id);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

}