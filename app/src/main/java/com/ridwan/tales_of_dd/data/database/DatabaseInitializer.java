package com.ridwan.tales_of_dd.data.database;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.ridwan.tales_of_dd.data.entities.Character;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;
import com.ridwan.tales_of_dd.data.entities.Narrative;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Initializes the database with sample data from a JSON file.
 * This class ensures that the database is populated only once during app lifecycle.
 */
public class DatabaseInitializer {
    private static final String TAG = "DatabaseInitializer";
    private static final String JSON_FILE = "sample_data.json";
    private static boolean isInitialized = false;

    /**
     * Data structure matching the JSON file format.
     * Used by Gson for deserializing the JSON data.
     */
    static class SampleData {
        List<Landmark> landmarks;
        List<Character> characters;
        List<LandmarkCharacter> landmark_character;
        List<Narrative> narratives;
    }

    /**
     * Initializes the database with sample data if not already initialized.
     *
     * @param context Application context for accessing assets
     * @param db Database instance to populate
     */
    public static void initializeDb(Context context, AppDatabase db) {
        if (isInitialized) {
            return;
        }

        try {
            // Load and parse JSON data
            String jsonString = loadJSONFromAsset(context, JSON_FILE);
            SampleData data = new Gson().fromJson(jsonString, SampleData.class);

            // Insert data in background thread
            new Thread(() -> {
                try {
                    // Clear existing data before insertion
                    clearExistingData(db);

                    // Insert all entities sequentially to maintain referential integrity
                    insertLandmarks(db, data.landmarks);
                    insertCharacters(db, data.characters);
                    insertRelationships(db, data.landmark_character);
                    insertNarratives(db, data.narratives);

                    isInitialized = true;
                } catch (Exception e) {
                    Log.e(TAG, "Database initialization failed: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Error loading sample data: " + e.getMessage());
        }
    }

    /**
     * Loads JSON file from assets folder.
     */
    private static String loadJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Failed to load JSON file: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Clears all existing data from the database.
     */
    private static void clearExistingData(AppDatabase db) {
        db.landmarkDao().deleteAll();
        db.characterDao().deleteAll();
        db.landmarkCharacterDao().deleteAll();
        db.narrativeDao().deleteAll();
    }

    /**
     * Inserts landmark data into the database.
     */
    private static void insertLandmarks(AppDatabase db, List<Landmark> landmarks) {
        for (Landmark landmark : landmarks) {
            db.landmarkDao().insertLandmark(landmark);
        }
    }

    /**
     * Inserts character data into the database.
     */
    private static void insertCharacters(AppDatabase db, List<Character> characters) {
        for (Character character : characters) {
            db.characterDao().insertCharacter(character);
        }
    }

    /**
     * Inserts landmark-character relationships into the database.
     */
    private static void insertRelationships(AppDatabase db, List<LandmarkCharacter> relationships) {
        for (LandmarkCharacter relation : relationships) {
            db.landmarkCharacterDao().insert(relation);
        }
    }

    /**
     * Inserts narrative data into the database.
     */
    private static void insertNarratives(AppDatabase db, List<Narrative> narratives) {
        for (Narrative narrative : narratives) {
            db.narrativeDao().insert(narrative);
        }
    }
}