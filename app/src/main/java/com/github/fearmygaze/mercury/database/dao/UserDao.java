package com.github.fearmygaze.mercury.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.github.fearmygaze.mercury.database.model.User1;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User1> getAll();

    @Query("DELETE FROM users")
    void deleteAll();

    @Query("DELETE from users WHERE id = :userId")
    void delete(String userId);

    @Query("SELECT * FROM users WHERE id = :userId")
    User1 getByID(String userId);

    @Query("UPDATE users SET notificationToken = :token WHERE id = :userId")
    void updateToken(String token, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User1 user);

    @Update
    void update(User1 user);

    @Delete
    void delete(User1 user);

    @Transaction
    default User1 transactionUpdateUser(User1 user) {
        update(user);
        return getByID(user.getId());
    }

    @Transaction
    default User1 transactionUpdateToken(String token, String id) {
        updateToken(token, id);
        return getByID(id);
    }

}
