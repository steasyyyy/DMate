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

@Entity(tableName = "analyses",
        foreignKeys = {@ForeignKey(
                entity = User.class,
                parentColumns = "uId",
                childColumns = "uIdF",
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = {@Index("uIdF")})
public class Analysis {

    @Ignore
    public static String timespanToExamineBasalEffect = "03:00:00";

    public Analysis() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer aId;

    public Integer uIdF;

    @TypeConverters(RoomConverter.class)
    private Timestamp timestamp;

    //basal analysis and notifications
    private String timespanToTriggerBasalNotification;
    private Float divergenceFromInitialValue;


    public Integer getaId() {
        return aId;
    }

    public Integer getuIdF() {
        return uIdF;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTimespanToTriggerBasalNotification() {
        return timespanToTriggerBasalNotification;
    }

    public Float getDivergenceFromInitialValue() {
        return divergenceFromInitialValue;
    }

    public void setaId(Integer aId) {
        this.aId = aId;
    }

    public void setuIdF(Integer uIdF) {
        this.uIdF = uIdF;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimespanToTriggerBasalNotification(String timespanToTriggerBasalNotification) {
        this.timespanToTriggerBasalNotification = timespanToTriggerBasalNotification;
    }

    public void setDivergenceFromInitialValue(Float divergenceFromInitialValue) {
        this.divergenceFromInitialValue = divergenceFromInitialValue;
    }
}
