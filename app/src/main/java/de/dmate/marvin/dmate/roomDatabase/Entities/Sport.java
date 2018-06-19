package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "sports")
public class Sport {

    public Sport() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer sId;

    private String sportName;
    private Float sportEffectPerUnit;


    //getter
    public Integer getsId() {
        return sId;
    }

    public String getSportName() {
        return sportName;
    }

    public Float getSportEffectPerUnit() {
        return sportEffectPerUnit;
    }

    //setter
    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public void setSportEffectPerUnit(Float sportEffectPerUnit) {
        this.sportEffectPerUnit = sportEffectPerUnit;
    }
}
