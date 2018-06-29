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
    public static Integer DAYTIMES_NOT_SET_WARNING = 3;
    @Ignore
    public static Integer TARGET_NOT_SET_WARNING = 4;
    @Ignore
    public static Integer BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING = 5;
    @Ignore
    public static Integer PLANNED_BASAL_INJECTIONS_NOT_SET = 6;
    @Ignore
    public static Integer BASAL_RATIO_ADJUST_INCREASE = 7;
    @Ignore
    public static Integer BASAL_RATIO_ADJUST_REDUCE = 8;

    @Ignore
    public static String MESSAGE_DAYTIMES_NOT_SET_WARNING = "Some entries could not be matched with defined daytimes. Please define non-overlapping daytimes for the whole day (24h) as the ratio wizard will not be available as long as the daytimes have not been set properly.";
    @Ignore
    public static String MESSAGE_TARGET_NOT_SET_WARNING = "Please define a blood sugar value target area in the settings as the ratio wizard will not be available as long as the target area has not been set.";
    @Ignore
    public static String MESSAGE_BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING = "Please define the duration of action of your bolus insulin as the ratio wizard will not be available as it has not been set.";
    @Ignore
    public static String MESSAGE_PLANNED_BASAL_INJECTIONS_NOT_SET = "Please define planned basal insulin injections as otherwise DMate will not be able to remind you of forgotten basal insulin injections.";
    @Ignore
    public static String MESSAGE_BASAL_RATIO_ADJUST_INCREASE = "Recently your blood sugar values seem to have risen without any apparent reason. You might want to consider increasing your basal insulin ratio.";
    @Ignore
    public static String MESSAGE_BASAL_RATIO_ADJUST_REDUCE = "Recently your blood sugar values seem to have fallen without any apparent reason. You might want to consider reducing your basal insulin ratio.";

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
