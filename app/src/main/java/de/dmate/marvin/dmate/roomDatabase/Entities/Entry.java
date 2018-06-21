package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.RoomConverter;

//declaring @Entity here specifies, that Room should create a table
//the colums of that table represent the attributes of this class
//CAREFUL: Objects are not supported natively! You need to create TypeConverters, to convert an object to a value as done in RoomConverter
@Entity(tableName = "entries",
        foreignKeys = {
        @ForeignKey(
                entity = Daytime.class,
                parentColumns = "dId",
                childColumns = "dIdF",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.SET_NULL)},
        indices = {@Index("dIdF")})
public class Entry {

    public Entry() {
        exercises = new ArrayList<>();
    }

    @PrimaryKey(autoGenerate = true)
    public Integer eId;

    public Integer dIdF;

    @TypeConverters(RoomConverter.class)
    private Timestamp timestamp = null;

    private Float bloodSugar = null;
    private Float breadUnit = null;
    private Float bolus = null;
    private Float basal = null;
    private String note = null;
    private Boolean reliable = false; //when the user is ill, this value will be false

    private Float reqBolusSimple = null;
    private Float reqBolusConsulting = null;
    private Float buFactorReal = null;
    private Float buFactorConsulting = null;
    private Float divergenceFromTarget = null;
    private Float bolusCorrectionByBloodSugar = null;
    private Float bolusCorrectionBySport = null;

    @Ignore
    private List<Exercise> exercises;

    //getter
    public Integer geteId() {
        return eId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Float getBloodSugar() {
        return bloodSugar;
    }

    public Float getBreadUnit() {
        return breadUnit;
    }

    public Float getBolus() {
        return bolus;
    }

    public Float getBasal() {
        return basal;
    }

    public String getNote() {
        return note;
    }

    public Boolean getReliable() {
        return reliable;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public Float getReqBolusSimple() {
        return reqBolusSimple;
    }

    public Float getReqBolusConsulting() {
        return reqBolusConsulting;
    }

    public Float getBuFactorReal() {
        return buFactorReal;
    }

    public Float getBuFactorConsulting() {
        return buFactorConsulting;
    }

    public Float getDivergenceFromTarget() {
        return divergenceFromTarget;
    }

    public Float getBolusCorrectionByBloodSugar() {
        return bolusCorrectionByBloodSugar;
    }

    public Float getBolusCorrectionBySport() {
        return bolusCorrectionBySport;
    }

    //setter
    public void seteId(Integer eId) {
        this.eId = eId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setBloodSugar(Float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public void setBreadUnit(Float breadUnit) {
        this.breadUnit = breadUnit;
    }

    public void setBolus(Float bolus) {
        this.bolus = bolus;
    }

    public void setBasal(Float basal) {
        this.basal = basal;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setReliable(Boolean reliable) {
        this.reliable = reliable;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public void setReqBolusSimple(Float reqBolusSimple) {
        this.reqBolusSimple = reqBolusSimple;
    }

    public void setReqBolusConsulting(Float reqBolusConsulting) {
        this.reqBolusConsulting = reqBolusConsulting;
    }

    public void setBuFactorReal(Float buFactorReal) {
        this.buFactorReal = buFactorReal;
    }

    public void setBuFactorConsulting(Float buFactorConsulting) {
        this.buFactorConsulting = buFactorConsulting;
    }

    public void setDivergenceFromTarget(Float divergenceFromTarget) {
        this.divergenceFromTarget = divergenceFromTarget;
    }

    public void setBolusCorrectionByBloodSugar(Float bolusCorrectionByBloodSugar) {
        this.bolusCorrectionByBloodSugar = bolusCorrectionByBloodSugar;
    }

    public void setBolusCorrectionBySport(Float bolusCorrectionBySport) {
        this.bolusCorrectionBySport = bolusCorrectionBySport;
    }
}