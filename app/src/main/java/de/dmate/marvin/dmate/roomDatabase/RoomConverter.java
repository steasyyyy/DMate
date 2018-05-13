package de.dmate.marvin.dmate.roomDatabase;

import android.arch.persistence.room.TypeConverter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RoomConverter {

//    @TypeConverter
//    public static Date toDate(Long timestamp) {
//        return timestamp == null ? null : new java.sql.Date(timestamp);
//    }
//
//    @TypeConverter
//    public static Long toTimestamp(Date date) {
//        return date == null ? null : date.getTime();
//    }

    @TypeConverter
    public static Timestamp stringToTimestamp(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        java.util.Date parsedDate;
        try {
            parsedDate = dateFormat.parse(timestamp);
        } catch (ParseException e) {
            System.out.println("Wrong string format to parse to Timestamp");
            parsedDate = new Timestamp(System.currentTimeMillis());
        }
        return timestamp == null ? null : new Timestamp(parsedDate.getTime());
    }

    @TypeConverter
    public static String timestampToString(Timestamp timestamp) {
        return timestamp.toString();
    }
}
