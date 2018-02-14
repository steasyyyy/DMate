package de.dmate.marvin.dmate;

/**
 * Created by Marvin on 14.02.2018.
 */

public class Value {

    public Integer id = null;
    public Integer bloodsugar = null;
    public Float breadunit = null;
    public Float bolus = null;
    public Float basal = null;
    public String note = null;

    //Helper class ValueBuilder is used to build Value objects avoiding to implement all permutations of constructors
    //USE LIKE THIS TO INSTANCIATE VALUE OBJECTS:
    //Value a = Value.bloodsugar(122).build();
    //Value b = Value.bloodsugar(100).bolus(15.0).note("testnote").build();
    //OR IF NECESSARY SAVE AS TEMPORARY VALUEBUILDER OBJECT.
    //Value.ValueBuilder temp = Value.id(i);
    //temp.bloodsugar(97).build();
    private Value(ValueBuilder vb) {
        this.id=Helper.getInstance().getApplication().getNextID();
        this.bloodsugar=vb.bloodsugar;
        this.breadunit=vb.breadunit;
        this.bolus=vb.bolus;
        this.basal=vb.basal;
        this.note=vb.note;
    }

    //for testing purpose only
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------------------------------------------------------------------------\nNEXT VALUE:\n");
        if (this.id!=null) sb.append("ID: " + this.id.toString() + "\n");
        if (this.bloodsugar!=null) sb.append("Bloodsugar: " + this.bloodsugar.toString() + "\n");
        if (this.breadunit!=null) sb.append("Breadunit: " + this.breadunit.toString() + "\n");
        if (this.bolus!=null) sb.append("Bolus: " + this.bolus.toString() + "\n");
        if (this.basal!=null) sb.append("Basal: " + this.basal.toString() + "\n");
        if (this.note!=null) sb.append("Note: " + this.note.toString() + "\n");
        sb.append("-------------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }

    public static ValueBuilder id(Integer id) {
        return new ValueBuilder().id(id);
    }

    public static ValueBuilder bloodsugar(Integer bloodsugar) {
        return new ValueBuilder().bloodsugar(bloodsugar);
    }

    public static ValueBuilder breadunit(Float breadunit) {
        return new ValueBuilder().breadunit(breadunit);
    }

    public static ValueBuilder bolus(Float bolus) {
        return new ValueBuilder().bolus(bolus);
    }

    public static ValueBuilder basal(Float basal) {
        return new ValueBuilder().basal(basal);
    }

    public static ValueBuilder note(String note) {
        return new ValueBuilder().note(note);
    }

    public static Value build() {
        return new ValueBuilder().build();
    }

    public static class ValueBuilder {
        private Integer id;
        private Integer bloodsugar;
        private Float breadunit;
        private Float bolus;
        private Float basal;
        private String note;
//
//        public ValueBuilder id(Integer id) {
//            this.id=id;
//            return this;
//        }

        public ValueBuilder id(Integer id) {
            this.id=id;
            return this;
        }

        public ValueBuilder bloodsugar(Integer bloodsugar){
            this.bloodsugar=bloodsugar;
            return this;
        }

        public ValueBuilder breadunit(Float breadunit) {
            this.breadunit=breadunit;
            return this;
        }

        public ValueBuilder bolus(Float bolus) {
            this.bolus=bolus;
            return this;
        }

        public ValueBuilder basal(Float basal) {
            this.basal=basal;
            return this;
        }

        public ValueBuilder note(String note) {
            this.note=note;
            return this;
        }

        public Value build() {
            return new Value(this);
        }
    }
}


