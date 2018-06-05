package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "plannedBasalInjections",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "uId",
                childColumns = "uIdF",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        indices = {@Index("uIdF")})

public class PlannedBasalInjection {

    public PlannedBasalInjection() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer pbiId;

    public Integer uIdF;

    private String timeOfDay;
    private Float basal;


    public Integer getPbiId() {
        return pbiId;
    }

    public Integer getuIdF() {
        return uIdF;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public Float getBasal() {
        return basal;
    }

    public void setPbiId(Integer pbiId) {
        this.pbiId = pbiId;
    }

    public void setuIdF(Integer uIdF) {
        this.uIdF = uIdF;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public void setBasal(Float basal) {
        this.basal = basal;
    }
}
