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

//    public boolean isLastEntryOfThisDay() {
//        //get entries from prefs
//        ArrayList<Entry> entries =  Helper.getInstance().getApplication().getAllEntries();
//        int indexOfThis = 0;
//
//        //easy option: entries.getIndexOf(this) does not work, because objects are recreated when reading from prefs
//        //so: if the millis are the same, the object must have the same values
//        for (Entry e : entries) {
//            if(e.getDateMillis() == this.getDateMillis()) {
//                indexOfThis = entries.indexOf(e);
//            }
//        }
//
//        //if index=0, there is no entry before "this" and "this" must be the last entry of the day
//        if (indexOfThis==0) return true;
//
//        //create calendars to make Year and Day of Year comparable
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(this.getDate());
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(entries.get(indexOfThis-1).getDate());
//        boolean sameDay = (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) ||
//                cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR));
//        return sameDay;
//    }

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