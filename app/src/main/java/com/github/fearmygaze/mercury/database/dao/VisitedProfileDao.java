package com.github.fearmygaze.mercury.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.fearmygaze.mercury.database.model.VisitedProfile;
import com.github.fearmygaze.mercury.database.model.User1;

import java.util.List;

@Dao
public interface VisitedProfileDao {

    @Query("SELECT * FROM visitedProfiles")
    List<VisitedProfile> getAll();

    @Query("SELECT * FROM visitedProfiles WHERE byUserId = :userId")
    List<VisitedProfile> getAllFor(String userId);

    @Query("DELETE FROM visitedProfiles")
    void deleteAll();

    @Query("DELETE FROM visitedProfiles WHERE byUserId = :userId")
    void deleteAllFor(String userId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(VisitedProfile visitedProfile);

    @Delete
    void delete(VisitedProfile visitedProfile);

    default void insert(String byUserId, User1 user1) {
        insert(new VisitedProfile(byUserId, user1.getId(), user1.getUsername(),
                user1.getImage(), user1.getNotificationToken()));
    }
}
