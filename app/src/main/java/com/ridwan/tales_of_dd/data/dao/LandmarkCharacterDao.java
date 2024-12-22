package com.ridwan.tales_of_dd.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;

import java.util.List;

/**
 * Data Access Object (DAO) for managing `LandmarkCharacter` entity operations.
 * Provides methods for database interactions related to relationships between landmarks and characters.
 */
@Dao
public interface LandmarkCharacterDao {

    /**
     * Retrieves all landmark-character relationships from the database.
     *
     * @return A list of all `LandmarkCharacter` relationships.
     */
    @Query("SELECT * FROM landmark_character")
    List<LandmarkCharacter> getAllLandmarkCharacters();

    /**
     * Counts the total number of landmark-character relationships in the database.
     *
     * @return The total number of relationships.
     */
    @Query("SELECT COUNT(*) FROM landmark_character")
    int getCount();

    /**
     * Retrieves all landmark-character relationships for a specific landmark ID.
     *
     * @param landmarkId The ID of the landmark.
     * @return A list of `LandmarkCharacter` relationships for the specified landmark.
     */
    @Query("SELECT * FROM landmark_character WHERE landmarkId = :landmarkId")
    List<LandmarkCharacter> getLandmarkCharactersByLandmarkId(int landmarkId);

    /**
     * Retrieves all landmark-character relationships for a specific character ID.
     *
     * @param characterId The ID of the character.
     * @return A list of `LandmarkCharacter` relationships for the specified character.
     */
    @Query("SELECT * FROM landmark_character WHERE characterId = :characterId")
    List<LandmarkCharacter> getLandmarkCharactersByCharacterId(int characterId);

    /**
     * Inserts a single landmark-character relationship into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param landmarkCharacter The `LandmarkCharacter` relationship to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LandmarkCharacter landmarkCharacter);

    /**
     * Inserts multiple landmark-character relationships into the database.
     * If a conflict occurs, the existing records will be replaced.
     *
     * @param landmarkCharacters The list of `LandmarkCharacter` relationships to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LandmarkCharacter> landmarkCharacters);

    /**
     * Deletes all landmark-character relationships from the database.
     */
    @Query("DELETE FROM landmark_character")
    void deleteAll();
}