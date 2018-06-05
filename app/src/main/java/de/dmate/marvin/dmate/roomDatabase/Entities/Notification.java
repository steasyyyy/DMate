package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Timestamp;

import de.dmate.marvin.dmate.roomDatabase.RoomConverter;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "notifications",
        foreignKeys = {@ForeignKey(
            entity = User.class,
            parentColumns = "uId",
            childColumns = "uIdF",
            onDelete = CASCADE,
            onUpdate = CASCADE)},
        indices = {@Index("uIdF")})
public class Notification {

    @Ignore
    public static Integer BASAL_ADJUST = 1;
    @Ignore
    public static Integer BASAL_INJECTION_FORGOTTEN = 2;

    public Notification() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer nId;

    public Integer uIdF;

    private Integer notificationType;
    private String message;

    @TypeConverters(RoomConverter.class)
    private Timestamp timestamp;

    public Integer getnId() {
        return nId;
    }

    public Integer getuIdF() {
        return uIdF;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setnId(Integer nId) {
        this.nId = nId;
    }

    public void setuIdF(Integer uIdF) {
        this.uIdF = uIdF;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
