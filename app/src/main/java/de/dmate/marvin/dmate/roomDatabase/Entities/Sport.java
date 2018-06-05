package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "sports",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "uId",
                childColumns = "uIdF",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        indices = {@Index("uIdF")})
public class Sport {

    public Sport() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer sId;

    public Integer uIdF;

    private String sportName;
    private Integer sportEffectPerUnit;


    public Integer getsId() {
        return sId;
    }

    public Integer getuIdF() {
        return uIdF;
    }

    public String getSportName() {
        return sportName;
    }

    public Integer getSportEffectPerUnit() {
        return sportEffectPerUnit;
    }

    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public void setuIdF(Integer uIdF) {
        this.uIdF = uIdF;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public void setSportEffectPerUnit(Integer sportEffectPerUnit) {
        this.sportEffectPerUnit = sportEffectPerUnit;
    }
}
