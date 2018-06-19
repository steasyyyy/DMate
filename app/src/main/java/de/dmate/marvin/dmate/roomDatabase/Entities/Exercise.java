package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "exercises",
        foreignKeys = {
        @ForeignKey(
                entity = Entry.class,
                parentColumns = "eId",
                childColumns = "eIdF",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        @ForeignKey(
                entity = Sport.class,
                parentColumns = "sId",
                childColumns = "sIdF",
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = {@Index("eIdF"), @Index("sIdF")})
public class Exercise {

    public Exercise() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer exId;

    public Integer eIdF;
    public Integer sIdF;

    private Float exerciseUnits;

    //getter
    public Integer getExId() {
        return exId;
    }

    public Integer geteIdF() {
        return eIdF;
    }

    public Integer getsIdF() {
        return sIdF;
    }

    public Float getExerciseUnits() {
        return exerciseUnits;
    }

    //setter
    public void setExId(Integer exId) {
        this.exId = exId;
    }

    public void seteIdF(Integer eIdF) {
        this.eIdF = eIdF;
    }

    public void setsIdF(Integer sIdF) {
        this.sIdF = sIdF;
    }

    public void setExerciseUnits(Float exerciseUnits) {
        this.exerciseUnits = exerciseUnits;
    }
}
