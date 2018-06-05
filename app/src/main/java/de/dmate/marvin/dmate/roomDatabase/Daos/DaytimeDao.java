package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;

@Dao
public interface DaytimeDao {

    @Query("SELECT * FROM daytimes ORDER BY daytimeStart")
    LiveData<List<Daytime>> getAllDaytimesByUserId(Integer userId);



}
