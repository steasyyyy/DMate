package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Timestamp;

import de.dmate.marvin.dmate.roomDatabase.RoomConverter;

@Entity(tableName = "observations")
public class Observation {

    public Observation() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer oId;

    @TypeConverters(RoomConverter.class)
    private Timestamp timestamp;

    private Integer initialValue;
    private Integer divergenceFromInitialValue;


    //getter
    public Integer getoId() {
        return oId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getInitialValue() {
        return initialValue;
    }

    public Integer getDivergenceFromInitialValue() {
        return divergenceFromInitialValue;
    }

    //setter
    public void setoId(Integer oId) {
        this.oId = oId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setInitialValue(Integer initialValue) {
        this.initialValue = initialValue;
    }

    public void setDivergenceFromInitialValue(Integer divergenceFromInitialValue) {
        this.divergenceFromInitialValue = divergenceFromInitialValue;
    }
}
