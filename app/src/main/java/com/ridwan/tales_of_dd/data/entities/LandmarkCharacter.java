package com.ridwan.tales_of_dd.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

/**
 * Junction entity representing the many-to-many relationship
 * between Landmarks and Characters.
 * Maps to the 'landmark_character' table in the database.
 */
@Entity(tableName = "landmark_character",
        primaryKeys = {"landmarkId", "characterId"},
        foreignKeys = {
                @ForeignKey(entity = Landmark.class,
                        parentColumns = "id",
                        childColumns = "landmarkId"),
                @ForeignKey(entity = Character.class,
                        parentColumns = "id",
                        childColumns = "characterId"),
        },
        indices = @Index(value = "characterId"))
public class LandmarkCharacter {
    /** ID of the associated landmark */
    @SerializedName("landmark_id")
    public int landmarkId;

    /** ID of the associated character */
    @SerializedName("character_id")
    public int characterId;
}