package de.dmate.marvin.dmate.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.dmate.marvin.dmate.util.Helper;

/**
 * Created by Marvin on 14.02.2018.
 */

public class Entry {
    private Date date = null;
    private Integer bloodsugar = null;
    private Float breadunit = null;
    private Float bolus = null;
    private Float basal = null;
    private String note = null;

    //Helper class EntryBuilder is used to build Entry objects avoiding to implement all permutations of constructors
    //USE LIKE THIS TO INSTANCIATE ENTRY OBJECTS:
    //Entry a = Entry.bloodsugar(122).build();
    //Entry b = Entry.bloodsugar(100).bolus(15.0).note("testnote").build();
    //OR IF NECESSARY SAVE AS TEMPORARY ENTRYBUILDER OBJECT:
    //Entry.EntryBuilder temp = Entry.bolus(32.5);
    //temp.bloodsugar(97).build();
    private Entry(EntryBuilder vb) {
        this.date = new Date(System.currentTimeMillis());
        this.bloodsugar=vb.bloodsugar;
        this.breadunit=vb.breadunit;
        this.bolus=vb.bolus;
        this.basal=vb.basal;
        this.note=vb.note;
        Helper.getInstance().getApplication().putEntry(this);
    }

    //for testing purpose only
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------------------------------------------------------------------------\nENTRY:");
        sb.append("\n" + "Date: ");
        if (this.date !=null) sb.append(this.date.toString());
        sb.append("\n" + "Date in millis: ");
        if (this.date !=null) sb.append(this.date.getTime());
        sb.append("\n" + "Is last entry of this day: ");
        if (this.date !=null) sb.append(isLastEntryOfThisDay());
        sb.append("\n" + "Bloodsugar: ");
        if (this.bloodsugar!=null) sb.append(this.bloodsugar.toString());
        sb.append("\n" + "Breadunit: ");
        if (this.breadunit!=null) sb.append(this.breadunit.toString());
        sb.append("\n" + "Bolus: ");
        if (this.bolus!=null) sb.append(this.bolus.toString());
        sb.append("\n" + "Basal: ");
        if (this.basal!=null) sb.append(this.basal.toString());
        sb.append("\n" + "Note: ");
        if (this.note!=null) sb.append(this.note.toString());
        sb.append("\n");
        sb.append("-------------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }

    //getters for all attributes
    public Date getDate() {
        return date;
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

    //watch out: entries are ordered "last created first"
    // -> we need the last entry of one day if we want to find out where to set the date separator
    // (one before the last entry of the same day)
    public boolean isLastEntryOfThisDay() {
        //get entries from prefs
        ArrayList<Entry> entries =  Helper.getInstance().getApplication().getAllEntries();
        int indexOfThis = 0;

        //easy option: entries.getIndexOf(this) does not work, because objects are recreated when reading from prefs
        //so: if the millis are the same, the object must have the same values
        for (Entry e : entries) {
            if(e.getDate().getTime() == this.getDate().getTime()) {
                indexOfThis = entries.indexOf(e);
            }
        }

        //if index=0, there is no entry before "this" and "this" must be the last entry of the day
        if (indexOfThis==0) return true;

        //create calendars to make Year and Day of Year comparable
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(this.date);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(entries.get(indexOfThis-1).getDate());
        boolean sameDay = (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) ||
                cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR));
        return sameDay;
    }

    //static functions to call when creating an entry via EntryBuilder
    public static EntryBuilder bloodsugar(Integer bloodsugar) {
        return new EntryBuilder().bloodsugar(bloodsugar);
    }

    public static EntryBuilder breadunit(Float breadunit) {
        return new EntryBuilder().breadunit(breadunit);
    }

    public static EntryBuilder bolus(Float bolus) {
        return new EntryBuilder().bolus(bolus);
    }

    public static EntryBuilder basal(Float basal) {
        return new EntryBuilder().basal(basal);
    }

    public static EntryBuilder note(String note) {
        return new EntryBuilder().note(note);
    }

    public static Entry build() {
        return new EntryBuilder().build();
    }

    //EntryBuilder to create instances of Entry
    //usage explained above
    public static class EntryBuilder {
        private Integer bloodsugar;
        private Float breadunit;
        private Float bolus;
        private Float basal;
        private String note;

        public EntryBuilder bloodsugar(Integer bloodsugar){
            this.bloodsugar=bloodsugar;
            return this;
        }

        public EntryBuilder breadunit(Float breadunit) {
            this.breadunit=breadunit;
            return this;
        }

        public EntryBuilder bolus(Float bolus) {
            this.bolus=bolus;
            return this;
        }

        public EntryBuilder basal(Float basal) {
            this.basal=basal;
            return this;
        }

        public EntryBuilder note(String note) {
            this.note=note;
            return this;
        }

        public Entry build() {
            return new Entry(this);
        }
    }
}


