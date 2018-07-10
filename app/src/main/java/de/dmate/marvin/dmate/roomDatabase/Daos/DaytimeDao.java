package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaytimeDao {
    //get all daytimes
    @Query("SELECT * FROM daytimes ORDER BY daytimeStart ASC")
    LiveData<List<Daytime>> getAllDaytimes();

    //get daytime by id
    @Query("SELECT * FROM daytimes WHERE dId = :dId")
    Daytime getDaytimeById(Integer dId);

    @Insert(onConflict = REPLACE)
    void insertDaytime(Daytime daytime);

    @Delete
    void deleteDaytime(Daytime daytime);
}
