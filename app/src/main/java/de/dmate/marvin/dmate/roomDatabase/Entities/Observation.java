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

    private Integer eIdStart;
    private Integer eIdEnd;
    private Float divergenceFromStart;


    public Integer getOId() {
        return oId;
    }

    public void setOId(Integer oId) {
        this.oId = oId;
    }

    public Integer getEIdStart() {
        return eIdStart;
    }

    public void setEIdStart(Integer eIdStart) {
        this.eIdStart = eIdStart;
    }

    public Integer getEIdEnd() {
        return eIdEnd;
    }

    public void setEIdEnd(Integer eIdEnd) {
        this.eIdEnd = eIdEnd;
    }

    public Float getDivergenceFromStart() {
        return divergenceFromStart;
    }

    public void setDivergenceFromStart(Float divergenceFromStart) {
        this.divergenceFromStart = divergenceFromStart;
    }
}
