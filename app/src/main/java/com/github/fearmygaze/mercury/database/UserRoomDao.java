package com.github.fearmygaze.mercury.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.github.fearmygaze.mercury.model.User;

import java.util.List;

@Dao
public interface UserRoomDao {

    @Query("SELECT * FROM USERS")
    List<User> getAll();

    @Query("DELETE FROM users")
    void deleteAll();

    @Query("SELECT * FROM users WHERE id = :id")
    User getByID(String id);

    @Query("UPDATE users SET notificationToken = :token WHERE id = :id")
    void updateToken(String token, String id);

    @Insert(onConflict = REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Transaction
    default User transactionUpdateUser(User user) {
        update(user);
        return getByID(user.getId());
    }

    @Transaction
    default User transactionUpdateToken(String token, String id) {
        updateToken(token, id);
        return getByID(id);
    }
}
