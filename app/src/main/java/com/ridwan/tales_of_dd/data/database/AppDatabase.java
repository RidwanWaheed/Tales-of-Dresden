package com.ridwan.tales_of_dd.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.ridwan.tales_of_dd.data.dao.CharacterDao;
import com.ridwan.tales_of_dd.data.dao.LandmarkCharacterDao;
import com.ridwan.tales_of_dd.data.dao.LandmarkDao;
import com.ridwan.tales_of_dd.data.dao.NarrativeDao;
import com.ridwan.tales_of_dd.data.entities.Character;
import com.ridwan.tales_of_dd.data.entities.Landmark;
import com.ridwan.tales_of_dd.data.entities.LandmarkCharacter;
import com.ridwan.tales_of_dd.data.entities.Narrative;

/**
 * Main database class for the application.
 * Implements the Singleton pattern to ensure a single database instance.
 */
@Database(entities = {
        Landmark.class,
        Character.class,
        LandmarkCharacter.class,
        Narrative.class,
}, version = 2)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "tales_dd_database.db";
    private static volatile AppDatabase INSTANCE;

    // Abstract methods to access DAOs
    public abstract LandmarkDao landmarkDao();
    public abstract CharacterDao characterDao();
    public abstract LandmarkCharacterDao landmarkCharacterDao();
    public abstract NarrativeDao narrativeDao();

    /**
     * Gets the singleton instance of the database.
     * Creates the database if it doesn't exist.
     *
     * @param context Application context
     * @return The singleton database instance
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .fallbackToDestructiveMigration() // Handles schema changes
                            .setJournalMode(JournalMode.TRUNCATE) // Optimizes database size
                            .allowMainThreadQueries()  // TODO: Remove this in production
                            .build();

                    // Initialize with sample data
                    DatabaseInitializer.initializeDb(context, INSTANCE);
                }
            }
        }
        return INSTANCE;
    }
}