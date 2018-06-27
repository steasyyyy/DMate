package de.dmate.marvin.dmate.roomDatabase;

import android.arch.persistence.room.TypeConverter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RoomConverter {

    @TypeConverter
    public static Timestamp stringToTimestamp(String timestamp) {
        if (timestamp != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            java.util.Date parsedDate = null;
            try {
                parsedDate = dateFormat.parse(timestamp);
            } catch (ParseException e) {
                System.out.println("Wrong string format to parse to Timestamp");
            }
            return new Timestamp(parsedDate.getTime());
        }
        return null;
    }

    @TypeConverter
    public static String timestampToString(Timestamp timestamp) {
        if (timestamp != null) return timestamp.toString();
        else return null;
    }
}
