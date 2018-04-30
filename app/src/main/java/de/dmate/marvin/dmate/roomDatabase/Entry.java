package de.dmate.marvin.dmate.roomDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Date;
import java.sql.Timestamp;

//declaring @Entity here specifies, that Room should create a table
//the colums of that table represent the attributes of this class
//CAREFUL: Objects are not supported natively! You need to create TypeConverters, to convert an object to a value as done in RoomConverter
@Entity(tableName = "entries")
public class Entry {

    public Entry() {

    }

    @PrimaryKey(autoGenerate = true)
    public int eId;

    @TypeConverters(RoomConverter.class)
    private Timestamp timestamp = null;

    private Integer bloodsugar = null;
    private Float breadunit = null;
    private Float bolus = null;
    private Float basal = null;
    private String note = null;
    private Boolean verified = false;

    public int geteId() {
        return eId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getBloodsugar() {
        return bloodsugar;
    }

    public Float getBreadunit() {
        return breadunit;
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

    public Boolean getVerified() {
        return verified;
    }

    public void seteId(int eId) {
        this.eId = eId;
    }

    public void setTimestamp (Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setBloodsugar(Integer bloodsugar) {
        this.bloodsugar = bloodsugar;
    }

    public void setBreadunit(Float breadunit) {
        this.breadunit = breadunit;
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

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

}