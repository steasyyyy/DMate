package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "plannedBasalInjections")
public class PlannedBasalInjection {

    public PlannedBasalInjection() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer pbiId;

    private String timeOfDay;
    private Float basal;

    //getter
    public Integer getPbiId() {
        return pbiId;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public Float getBasal() {
        return basal;
    }

    //setter
    public void setPbiId(Integer pbiId) {
        this.pbiId = pbiId;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public void setBasal(Float basal) {
        this.basal = basal;
    }
}
