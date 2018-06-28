package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Timestamp;

import de.dmate.marvin.dmate.roomDatabase.RoomConverter;

@Entity(tableName = "notifications")
public class Notification {

    @Ignore
    public static Integer BASAL_INJECTION_FORGOTTEN = 1;
    @Ignore
    public static Integer BASAL_RATIO_ADJUST = 2;
    @Ignore
    public static Integer DAYTIME_WARNING = 3;
    @Ignore
    public static Integer TARGET_NOT_SET_WARNING = 4;
    @Ignore
    public static Integer BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING = 5;

    @Ignore
    public static String MESSAGE_DAYTIME_WARNING = "Some entries could not be matched with defined daytimes. Please define non-overlapping daytimes for the whole day (24h) as the ratio wizard will not be available as long as the daytimes have not been set properly";
    @Ignore
    public static String MESSAGE_TARGET_NOT_SET_WARNING = "Please define a blood sugar value target area in the settings as the ratio wizard will not be available as long as the target area has not been set";
    @Ignore
    public static String MESSAGE_BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING = "Please define the duration of action of your bolus insulin as the ratio wizard will not be available as it has not been set";

    public Notification() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer nId;

    @TypeConverters(RoomConverter.class)
    private Timestamp timestamp;

    private Integer notificationType;
    private String message;


    //getter
    public Integer getnId() {
        return nId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    //setter
    public void setnId(Integer nId) {
        this.nId = nId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
