package com.ridwan.tales_of_dd.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Entity representing a historical character in the application.
 * Maps to the 'characters' table in the database.
 */
@Entity(tableName = "characters")
public class Character {
    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Character's full name */
    public String name;

    /** Historical era the character belongs to */
    public String era;

    /** Brief description of character's personality */
    public String personality;

    /** Brief introduction of character's background */
    public String overview;

    /** URL to character's portrait image */
    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    public String imageUrl;
}