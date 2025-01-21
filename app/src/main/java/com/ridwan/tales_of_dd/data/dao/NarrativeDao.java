package com.ridwan.tales_of_dd.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ridwan.tales_of_dd.data.entities.Narrative;

import java.util.List;

/**
 * Data Access Object (DAO) for managing `Narrative` entity operations.
 * Provides methods for database interactions related to narratives.
 */
@Dao
public interface NarrativeDao {

    /**
     * Retrieves all narratives from the database.
     *
     * @return A list of all `Narrative` entities.
     */
    @Query("SELECT * FROM narratives")
    List<Narrative> getAllNarratives();

    /**
     * Counts the total number of narratives in the database.
     *
     * @return The total number of narratives.
     */
    @Query("SELECT COUNT(*) FROM narratives")
    int getCount();

    /**
     * Retrieves all narratives associated with a specific landmark.
     *
     * @param landmarkId The ID of the landmark.
     * @return A list of `Narrative` entities linked to the specified landmark.
     */
    @Query("SELECT * FROM narratives WHERE landmark_id = :landmarkId")
    List<Narrative> getNarrativesByLandmarkId(int landmarkId);

    /**
     * Retrieves all narratives associated with a specific character.
     *
     * @param characterId The ID of the character.
     * @return A list of `Narrative` entities linked to the specified character.
     */
    @Query("SELECT * FROM narratives WHERE character_id = :characterId")
    List<Narrative> getNarrativesByCharacterId(int characterId);

    /**
     * Retrieves the narrative text associated with a specific guide (character) and landmark.
     *
     * @param characterId The ID of the character (guide).
     * @param landmarkId  The ID of the landmark.
     * @return The narrative text, or null if no match is found.
     */
    @Query("SELECT narrative_text FROM narratives WHERE character_id = :characterId AND landmark_id = :landmarkId LIMIT 1")
    String getNarrativeForGuideAndLandmark(int characterId, int landmarkId);

    /**
     * Retrieves the narrative summary associated with a specific guide (character) and landmark.
     *
     * @param characterId The ID of the character (guide).
     * @param landmarkId  The ID of the landmark.
     * @return The narrative summary, or null if no match is found.
     */
    @Query("SELECT narrative_sum FROM narratives WHERE character_id = :characterId AND landmark_id = :landmarkId LIMIT 1")
    String getNarrativeSumForGuideAndLandmark(int characterId, int landmarkId);

    /**
     * Inserts a single narrative into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param narrative The `Narrative` entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Narrative narrative);

    /**
     * Inserts multiple narratives into the database.
     * If a conflict occurs, the existing records will be replaced.
     *
     * @param narratives The list of `Narrative` entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Narrative> narratives);

    /**
     * Updates an existing narrative in the database.
     *
     * @param narrative The `Narrative` entity with updated data.
     */
    @Update
    void update(Narrative narrative);

    /**
     * Deletes all narratives from the database.
     */
    @Query("DELETE FROM narratives")
    void deleteAll();
}