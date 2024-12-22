package com.ridwan.tales_of_dd.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ridwan.tales_of_dd.data.entities.UserProgress;

import java.util.List;

/**
 * Data Access Object (DAO) for managing `UserProgress` entity operations.
 * Provides methods for database interactions related to user progress tracking.
 */
@Dao
public interface UserProgressDao {

    /**
     * Retrieves all user progress entries from the database.
     *
     * @return A list of all `UserProgress` entities.
     */
    @Query("SELECT * FROM user_progress")
    List<UserProgress> getAllUserProgress();

    /**
     * Retrieves the progress data for a specific landmark.
     *
     * @param landmarkId The ID of the landmark.
     * @return The `UserProgress` entity for the specified landmark.
     */
    @Query("SELECT * FROM user_progress WHERE landmarkId = :landmarkId")
    UserProgress getUserProgressForLandmark(int landmarkId);

    /**
     * Inserts a new progress entry into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param userProgress The `UserProgress` entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProgress userProgress);

    /**
     * Updates an existing user progress entry in the database.
     *
     * @param userProgress The `UserProgress` entity with updated data.
     */
    @Update
    void update(UserProgress userProgress);

    /**
     * Deletes a specific user progress entry from the database.
     *
     * @param userProgress The `UserProgress` entity to delete.
     */
    @Delete
    void delete(UserProgress userProgress);

    /**
     * Counts the total number of unlocked landmarks.
     *
     * @return The total number of unlocked landmarks.
     */
    @Query("SELECT COUNT(*) FROM user_progress WHERE is_unlocked = 1")
    int getUnlockedLandmarksCount();

    /**
     * Deletes all progress entries from the database.
     */
    @Query("DELETE FROM user_progress")
    void deleteAll();
}