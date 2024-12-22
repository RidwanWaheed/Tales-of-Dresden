package com.ridwan.tales_of_dd.data.database;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Type converter for Room database to handle Date objects.
 * Converts between Date objects and Long timestamps for database storage.
 */
public class DateConverter {

    /**
     * Converts a timestamp (Long) to a Date object.
     * Used when reading dates from the database.
     *
     * @param timestamp The timestamp in milliseconds
     * @return Date object, or null if timestamp is null
     */
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    /**
     * Converts a Date object to a timestamp (Long).
     * Used when writing dates to the database.
     *
     * @param date The Date object to convert
     * @return Timestamp in milliseconds, or null if date is null
     */
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}