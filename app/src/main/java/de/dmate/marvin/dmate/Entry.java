package de.dmate.marvin.dmate;

import java.util.Date;

/**
 * Created by Marvin on 14.02.2018.
 */

public class Entry {

    public Date date = null;
    public Integer id = null;
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
        sb.append("-------------------------------------------------------------------------------------------------------\nNEXT ENTRY:\n");
        if (this.id!=null) sb.append("ID: " + this.id.toString() + "\n");
        if (this.date !=null) sb.append("Date: " + this.date.toString() + "\n");
        if (this.bloodsugar!=null) sb.append("Bloodsugar: " + this.bloodsugar.toString() + "\n");
        if (this.breadunit!=null) sb.append("Breadunit: " + this.breadunit.toString() + "\n");
        if (this.bolus!=null) sb.append("Bolus: " + this.bolus.toString() + "\n");
        if (this.basal!=null) sb.append("Basal: " + this.basal.toString() + "\n");
        if (this.note!=null) sb.append("Note: " + this.note.toString() + "\n");
        sb.append("-------------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }

//    //only used when reading existing entries from prefs
//    public static EntryBuilder id(Integer id) {
//        return new EntryBuilder().id(id);
//    }

//    //only used when reading existing entries from prefs
//    public static EntryBuilder timestamp(Date date) {
//        return new EntryBuilder().date(date);
//    }

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
//        private Integer id;
//        private Date date;
        private Integer bloodsugar;
        private Float breadunit;
        private Float bolus;
        private Float basal;
        private String note;

//        public EntryBuilder id(Integer id) {
//            this.id=id;
//            return this;
//        }
//
//        public EntryBuilder date(Date date) {
//            this.date=date;
//            return this;
//        }

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


