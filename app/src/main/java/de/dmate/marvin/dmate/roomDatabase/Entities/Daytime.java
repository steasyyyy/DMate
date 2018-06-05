package de.dmate.marvin.dmate.roomDatabase.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "daytimes",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "uId",
                childColumns = "uIdF",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        indices = {@Index("uIdF")})
public class Daytime {

    public Daytime() {

    }

    @PrimaryKey(autoGenerate = true)
    public Integer dId;

    public Integer uIdF;

    private String daytimeStart;
    private String daytimeEnd;
    private Integer correctionFactor;
    private Integer buFactor;

    public Integer getdId() {
        return dId;
    }

    public int getuIdF() {
        return uIdF;
    }

    public String getDaytimeStart() {
        return daytimeStart;
    }

    public String getDaytimeEnd() {
        return daytimeEnd;
    }

    public Integer getCorrectionFactor() {
        return correctionFactor;
    }

    public Integer getBuFactor() {
        return buFactor;
    }

    public void setdId(Integer dId) {
        this.dId = dId;
    }

    public void setuId(int uIdF) {
        this.uIdF = uIdF;
    }

    public void setDaytimeStart(String daytimeStart) {
        this.daytimeStart = daytimeStart;
    }

    public void setDaytimeEnd(String daytimeEnd) {
        this.daytimeEnd = daytimeEnd;
    }

    public void setCorrectionFactor(Integer correctionFactor) {
        this.correctionFactor = correctionFactor;
    }

    public void setBuFactor(Integer buFactor) {
        this.buFactor = buFactor;
    }
}
