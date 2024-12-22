package com.ridwan.tales_of_dd.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity tracking user progress and interactions with landmarks.
 * Maps to the 'user_progress' table in the database.
 */
@Entity(tableName = "user_progress",
        foreignKeys = @ForeignKey(entity = Landmark.class,
                parentColumns = "id",
                childColumns = "landmarkId"))
public class UserProgress {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "landmarkId")
    private int landmarkId; // ID of the landmark.

    @ColumnInfo(name = "is_unlocked")
    private boolean isUnlocked; // Whether the landmark is unlocked.

    // Getters and Setters (required for Room)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(int landmarkId) {
        this.landmarkId = landmarkId;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}