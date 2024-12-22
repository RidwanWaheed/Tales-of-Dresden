package com.ridwan.tales_of_dd.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Entity representing a historical landmark or point of interest.
 * Maps to the 'landmarks' table in the database.
 */
@Entity(tableName = "landmarks")
public class Landmark {
    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Name of the landmark */
    public String name;

    /** Geographical latitude */
    public float latitude;

    /** Geographical longitude */
    public float longitude;

    /** Detailed description of the landmark */
    public String description;

    /** URL to landmark's image */
    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    public String imageUrl;

    /** Indicates if this is a hidden/secret location */
    @SerializedName("is_secret")
    @ColumnInfo(name = "is_secret")
    public boolean isSecret;

    /** Reward for discovering this landmark */
    public String reward;

    /** Radius in meters for proximity detection */
    @SerializedName("buffer_radius")
    @ColumnInfo(name = "buffer_radius")
    public float bufferRadius;
}