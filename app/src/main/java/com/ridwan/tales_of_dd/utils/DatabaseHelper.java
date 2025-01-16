package com.ridwan.tales_of_dd.utils;

import android.content.Context;
import android.util.Log;

import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    /**
     * Retrieves all landmarks from the database.
     *
     * @param context the application context
     * @return a list of Landmark objects
     */
    public static List<Landmark> getAllLandmarks(Context context) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            return db.landmarkDao().getAllLandmarks();
        } catch (Exception e) {
            Log.e(TAG, "Error fetching landmarks from database", e);
            return null;
        }
    }


    /**
     * Retrieves all LandmarkCharacters associated with a specific guide item ID.
     *
     * @param context     the application context
     * @param characterId the ID of the guide item (character)
     * @return a list of LandmarkCharacter objects
     */
    public static List<LandmarkCharacter> getLandmarkCharactersByGuideItemId(Context context, int characterId) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            return db.landmarkCharacterDao().getLandmarkCharactersByCharacterId(characterId);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching LandmarkCharacters", e);
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a Landmark by its ID.
     *
     * @param context the application context
     * @param landmarkId the ID of the landmark
     * @return the Landmark object, or null if not found
     */
    public static Landmark getLandmarkById(Context context, int landmarkId) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            return db.landmarkDao().getLandmarkById(landmarkId);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching landmark by ID", e);
            return null;
        }
    }

    /**
     * Retrieves all LandmarkCharacters associated with a specific character ID.
     *
     * @param context the application context
     * @param characterId the ID of the character
     * @return a list of LandmarkCharacter objects
     */
    public static List<LandmarkCharacter> getLandmarkCharactersByCharacterId(Context context, int characterId) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            return db.landmarkCharacterDao().getLandmarkCharactersByCharacterId(characterId);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching landmark characters by character ID", e);
            return null;
        }
    }

    /**
     * Retrieves a Landmark by its name.
     *
     * @param context the application context
     * @param name the name of the landmark
     * @return the Landmark object, or null if not found
     */
    public static Landmark getLandmarkByName(Context context, String name) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            return db.landmarkDao().getLandmarkByName(name);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching landmark by name", e);
            return null;
        }
    }

    /**
     * Retrieves the narrative text for a specific guide (character) and landmark.
     *
     * @param context      the application context
     * @param characterId  the ID of the guide (character)
     * @param landmarkId   the ID of the landmark
     * @return the narrative text, or null if not found
     */
    public static String getNarrativeForGuideAndLandmark(Context context, int characterId, int landmarkId) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            return db.narrativeDao().getNarrativeForGuideAndLandmark(characterId, landmarkId);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching narrative for guide and landmark", e);
            return null;
        }
    }

}