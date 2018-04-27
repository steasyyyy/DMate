package de.dmate.marvin.dmate.roomDatabase;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class RoomConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
