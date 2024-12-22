package com.ridwan.tales_of_dd.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ridwan.tales_of_dd.data.entities.Landmark;

import java.util.List;

/**
 * Data Access Object (DAO) for managing `Landmark` entity operations.
 * Provides methods for database interactions related to landmarks.
 */
@Dao
public interface LandmarkDao {

    /**
     * Retrieves all landmarks from the database.
     *
     * @return A list of all `Landmark` entities.
     */
    @Query("SELECT * FROM landmarks")
    List<Landmark> getAllLandmarks();

    /**
     * Retrieves the IDs of all landmarks from the database.
     *
     * @return A list of landmark IDs.
     */
    @Query("SELECT id FROM landmarks")
    List<Integer> getAllLandmarkIds();

    /**
     * Counts the total number of landmarks in the database.
     *
     * @return The total number of landmarks.
     */
    @Query("SELECT COUNT(*) FROM landmarks")
    int getCount();

    /**
     * Retrieves a specific landmark by its ID.
     *
     * @param id The ID of the landmark to retrieve.
     * @return The `Landmark` entity with the specified ID.
     */
    @Query("SELECT * FROM landmarks WHERE id = :id")
    Landmark getLandmarkById(int id);

    /**
     * Inserts a single landmark into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param landmark The `Landmark` entity to insert.
     * @return The row ID of the inserted entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLandmark(Landmark landmark);

    /**
     * Inserts multiple landmarks into the database.
     * If a conflict occurs, the existing records will be replaced.
     *
     * @param landmarks The list of `Landmark` entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllLandmarks(List<Landmark> landmarks);

    /**
     * Updates an existing landmark in the database.
     *
     * @param landmark The `Landmark` entity with updated data.
     */
    @Update
    void updateLandmark(Landmark landmark);

    /**
     * Deletes a specific landmark from the database.
     *
     * @param landmark The `Landmark` entity to delete.
     */
    @Delete
    void deleteLandmark(Landmark landmark);

    /**
     * Deletes all landmarks from the database.
     */
    @Query("DELETE FROM landmarks")
    void deleteAll();
}