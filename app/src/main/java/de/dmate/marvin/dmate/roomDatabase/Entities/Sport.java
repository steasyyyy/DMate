package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "sports")
public class Sport {

    public Sport() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer sId;

    private String sportName;
    private Integer sportEffectPerUnit;


    //getter
    public Integer getsId() {
        return sId;
    }

    public String getSportName() {
        return sportName;
    }

    public Integer getSportEffectPerUnit() {
        return sportEffectPerUnit;
    }

    //setter
    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public void setSportEffectPerUnit(Integer sportEffectPerUnit) {
        this.sportEffectPerUnit = sportEffectPerUnit;
    }
}
