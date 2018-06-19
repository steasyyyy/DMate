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
