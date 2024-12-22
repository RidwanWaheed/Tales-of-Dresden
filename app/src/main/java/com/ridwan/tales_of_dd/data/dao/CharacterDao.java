package com.ridwan.tales_of_dd.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ridwan.tales_of_dd.data.entities.Character;

import java.util.List;

/**
 * Data Access Object (DAO) for managing `Character` entity operations.
 * Provides methods for database interactions such as querying, inserting, updating, and deleting characters.
 */
@Dao
public interface CharacterDao {

    /**
     * Retrieves all characters from the database.
     *
     * @return A list of all characters.
     */
    @Query("SELECT * FROM characters")
    List<Character> getAllCharacters();

    /**
     * Retrieves the IDs of all characters from the database.
     *
     * @return A list of all character IDs.
     */
    @Query("SELECT id FROM characters")
    List<Integer> getAllCharacterIds();

    /**
     * Counts the total number of characters in the database.
     *
     * @return The total number of characters.
     */
    @Query("SELECT COUNT(*) FROM characters")
    int getCount();

    /**
     * Retrieves a character by its ID.
     *
     * @param id The ID of the character to retrieve.
     * @return The character entity with the specified ID.
     */
    @Query("SELECT * FROM characters WHERE id = :id")
    Character getCharacterById(int id);

    /**
     * Inserts a single character into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param character The character to insert.
     * @return The ID of the newly inserted character.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCharacter(Character character);

    /**
     * Inserts multiple characters into the database.
     * If a conflict occurs, the existing records will be replaced.
     *
     * @param characters The list of characters to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCharacters(List<Character> characters);

    /**
     * Updates an existing character in the database.
     *
     * @param character The character entity to update.
     */
    @Update
    void updateCharacter(Character character);

    /**
     * Deletes a specific character from the database.
     *
     * @param character The character entity to delete.
     */
    @Delete
    void deleteCharacter(Character character);

    /**
     * Deletes all characters from the database.
     */
    @Query("DELETE FROM characters")
    void deleteAll();
}