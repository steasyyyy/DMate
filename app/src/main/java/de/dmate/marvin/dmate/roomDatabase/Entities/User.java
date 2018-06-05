package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "users")
public class User {

    public User() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer uId;

    private String name;
    private String bolusName;
    private Float bolusDuration;
    private String basalName;
    private Float basalDuration;

    private Integer targetMin;
    private Integer targetMax;
    private Boolean unitBu;
    private Boolean unitMgdl;
    private Boolean notificationsEnabled;

    private Float buFactorConsultingArithMean;

    private Float divergenceFromInitialValueArithMean;

    private Float bloodsugarArithMean;

    @Ignore
    private List<PlannedBasalInjection> plannedBasalInjections;
    @Ignore
    private List<Daytime> daytimes;
    @Ignore
    private List<Sport> sports;
    @Ignore
    private List<Analysis> analyses;





    public Integer getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getBolusName() {
        return bolusName;
    }

    public Float getBolusDuration() {
        return bolusDuration;
    }

    public String getBasalName() {
        return basalName;
    }

    public Float getBasalDuration() {
        return basalDuration;
    }

    public Integer getTargetMin() {
        return targetMin;
    }

    public Integer getTargetMax() {
        return targetMax;
    }

    public Boolean getUnitBu() {
        return unitBu;
    }

    public Boolean getUnitMgdl() {
        return unitMgdl;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public Float getBuFactorConsultingArithMean() {
        return buFactorConsultingArithMean;
    }

    public Float getDivergenceFromInitialValueArithMean() {
        return divergenceFromInitialValueArithMean;
    }

    public Float getBloodsugarArithMean() {
        return bloodsugarArithMean;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBolusName(String bolusName) {
        this.bolusName = bolusName;
    }

    public void setBolusDuration(Float bolusDuration) {
        this.bolusDuration = bolusDuration;
    }

    public void setBasalName(String basalName) {
        this.basalName = basalName;
    }

    public void setBasalDuration(Float basalDuration) {
        this.basalDuration = basalDuration;
    }

    public void setTargetMin(Integer targetMin) {
        this.targetMin = targetMin;
    }

    public void setTargetMax(Integer targetMax) {
        this.targetMax = targetMax;
    }

    public void setUnitBu(Boolean unitBu) {
        this.unitBu = unitBu;
    }

    public void setUnitMgdl(Boolean unitMgdl) {
        this.unitMgdl = unitMgdl;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public void setBuFactorConsultingArithMean(Float buFactorConsultingArithMean) {
        this.buFactorConsultingArithMean = buFactorConsultingArithMean;
    }

    public void setDivergenceFromInitialValueArithMean(Float divergenceFromInitialValueArithMean) {
        this.divergenceFromInitialValueArithMean = divergenceFromInitialValueArithMean;
    }

    public void setBloodsugarArithMean(Float bloodsugarArithMean) {
        this.bloodsugarArithMean = bloodsugarArithMean;
    }
}
