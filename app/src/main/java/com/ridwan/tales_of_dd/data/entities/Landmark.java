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
    private int id;

    /** Name of the landmark */
    private String name;

    /** Geographical latitude */
    private float latitude;

    /** Geographical longitude */
    private float longitude;

    /** Detailed description of the landmark */
    private String description;

    /** URL to the landmark's image */
    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    /** Indicates if this is a hidden/secret location */
    @SerializedName("is_secret")
    @ColumnInfo(name = "is_secret")
    private boolean isSecret;

    /** Reward for discovering this landmark */
    private String reward;

    /** Radius in meters for proximity detection */
    @SerializedName("buffer_radius")
    @ColumnInfo(name = "buffer_radius")
    private float bufferRadius;

    /**
     * Constructor for creating a Landmark instance.
     *
     * @param id           Unique identifier for the landmark.
     * @param name         Name of the landmark.
     * @param latitude     Geographical latitude.
     * @param longitude    Geographical longitude.
     * @param description  Detailed description of the landmark.
     * @param imageUrl     URL to the landmark's image.
     * @param isSecret     Indicates if the landmark is a hidden location.
     * @param reward       Reward for discovering this landmark.
     * @param bufferRadius Radius for proximity detection in meters.
     */
    public Landmark(int id, String name, float latitude, float longitude, String description,
                    String imageUrl, boolean isSecret, String reward, float bufferRadius) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isSecret = isSecret;
        this.reward = reward;
        this.bufferRadius = bufferRadius;
    }

    // Getters and Setters

    /** @return Unique identifier for the landmark */
    public int getId() {
        return id;
    }

    /** @param id Sets the unique identifier for the landmark */
    public void setId(int id) {
        this.id = id;
    }

    /** @return Name of the landmark */
    public String getName() {
        return name;
    }

    /** @param name Sets the name of the landmark */
    public void setName(String name) {
        this.name = name;
    }

    /** @return Geographical latitude */
    public float getLatitude() {
        return latitude;
    }

    /** @param latitude Sets the geographical latitude */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /** @return Geographical longitude */
    public float getLongitude() {
        return longitude;
    }

    /** @param longitude Sets the geographical longitude */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /** @return Detailed description of the landmark */
    public String getDescription() {
        return description;
    }

    /** @param description Sets the detailed description of the landmark */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return URL to the landmark's image */
    public String getImageUrl() {
        return imageUrl;
    }

    /** @param imageUrl Sets the URL to the landmark's image */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /** @return True if this is a hidden/secret location */
    public boolean isSecret() {
        return isSecret;
    }

    /** @param secret Sets whether the landmark is a hidden/secret location */
    public void setSecret(boolean secret) {
        isSecret = secret;
    }

    /** @return Reward for discovering this landmark */
    public String getReward() {
        return reward;
    }

    /** @param reward Sets the reward for discovering this landmark */
    public void setReward(String reward) {
        this.reward = reward;
    }

    /** @return Radius in meters for proximity detection */
    public float getBufferRadius() {
        return bufferRadius;
    }

    /** @param bufferRadius Sets the radius for proximity detection in meters */
    public void setBufferRadius(float bufferRadius) {
        this.bufferRadius = bufferRadius;
    }
}