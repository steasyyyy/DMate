package de.dmate.marvin.dmate.entities;

import java.util.Date;

import de.dmate.marvin.dmate.util.Helper;

/**
 * Created by Marvin on 14.02.2018.
 */

public class Entry {

    public Date date = null;
    public Integer bloodsugar = null;
    public Float breadunit = null;
    public Float bolus = null;
    public Float basal = null;
    public String note = null;

    //Helper class EntryBuilder is used to build Entry objects avoiding to implement all permutations of constructors
    //USE LIKE THIS TO INSTANCIATE ENTRY OBJECTS:
    //Entry a = Entry.bloodsugar(122).build();
    //Entry b = Entry.bloodsugar(100).bolus(15.0).note("testnote").build();
    //OR IF NECESSARY SAVE AS TEMPORARY ENTRYBUILDER OBJECT.
    //Entry.EntryBuilder temp = Entry.id(i);
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

    public static class EntryBuilder {
//        private Date date;
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


