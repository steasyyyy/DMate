package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "daytimes")
public class Daytime {

    public Daytime() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer dId;

    private String daytimeStart;
    private String daytimeEnd;
    private Float correctionFactor;
    private Float buFactor;
    private Float buFactorConsultingArithMean;

    //getter
    public Integer getdId() {
        return dId;
    }

    public String getDaytimeStart() {
        return daytimeStart;
    }

    public String getDaytimeEnd() {
        return daytimeEnd;
    }

    public Float getCorrectionFactor() {
        return correctionFactor;
    }

    public Float getBuFactor() {
        return buFactor;
    }

    public Float getBuFactorConsultingArithMean() {
        return buFactorConsultingArithMean;
    }

    //setter
    public void setdId(Integer dId) {
        this.dId = dId;
    }

    public void setDaytimeStart(String daytimeStart) {
        this.daytimeStart = daytimeStart;
    }

    public void setDaytimeEnd(String daytimeEnd) {
        this.daytimeEnd = daytimeEnd;
    }

    public void setCorrectionFactor(Float correctionFactor) {
        this.correctionFactor = correctionFactor;
    }

    public void setBuFactor(Float buFactor) {
        this.buFactor = buFactor;
    }

    public void setBuFactorConsultingArithMean(Float buFactorConsultingArithMean) {
        this.buFactorConsultingArithMean = buFactorConsultingArithMean;
    }
}
