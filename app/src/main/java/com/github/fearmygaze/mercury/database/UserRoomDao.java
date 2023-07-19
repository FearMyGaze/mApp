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
public interface UserRoomDao {

    @Query("SELECT * FROM USERS")
    List<User> getAllUsers();

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserByUserID(String id);

    @Query("UPDATE users SET notificationToken = :token WHERE id = :id")
    void updateUserToken(String token, String id);

    @Query("UPDATE users SET image = :image WHERE id = :id")
    void updateUserImage(String image, String id);

    @Query("UPDATE users SET isProfileOpen = :state WHERE id = :id")
    void updateProfileState(boolean state, String id);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

}