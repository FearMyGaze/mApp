package com.fearmygaze.mApp.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fearmygaze.mApp.model.User1;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User1> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    User1 getUserByID(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    User1 getUserByEmail(String email);

    @Query("SELECT imageUrl FROM users WHERE id = :id")
    String getImageUrl(int id);

    @Query("UPDATE users SET imageUrl= :url WHERE id = :id")
    void updateImageByID(String url, int id);

    @Query("DELETE FROM users WHERE id= :id")
    void deleteUserByID(int id);

    @Insert(onConflict = REPLACE)
    void insertUser(User1 user);

    @Update
    void updateUser(User1 user);

    @Delete
    void deleteUser(User1 user);

}