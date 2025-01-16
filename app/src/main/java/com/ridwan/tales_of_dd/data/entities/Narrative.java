package com.ridwan.tales_of_dd.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Entity representing a narrative or story told by a character about a landmark.
 * Maps to the 'narratives' table in the database.
 */
@Entity(tableName = "narratives",
        foreignKeys = {
                @ForeignKey(
                        entity = Character.class,
                        parentColumns = "id",
                        childColumns = "character_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Landmark.class,
                        parentColumns = "id",
                        childColumns = "landmark_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = "character_id"),
                @Index(value = "landmark_id")
        })
public class Narrative {
    @PrimaryKey
    public int id;

    /** ID of the character telling the narrative */
    @ColumnInfo(name = "character_id")
    @SerializedName("character_id")
    public int characterId;

    /** ID of the landmark the narrative is about */
    @ColumnInfo(name = "landmark_id")
    @SerializedName("landmark_id")
    public int landmarkId;

    /** The narrative text content */
    @ColumnInfo(name = "narrative_text")
    @SerializedName("narrative_text")
    public String narrativeText;

    /** URL to the audio version of the narrative */
    @ColumnInfo(name = "audio_url")
    @SerializedName("audio_url")
    public String audioUrl;
}