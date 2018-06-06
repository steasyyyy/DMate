package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface SportDao {

    //get all sports
    @Query("SELECT * FROM sports")
    LiveData<List<Sport>> getAllSports();

    //get sport by Id
    @Query("SELECT * FROM sports WHERE sId = :sportId")
    Sport getSportById(Integer sportId);

    @Insert(onConflict = REPLACE)
    void insertSport(Sport sport);

    @Delete
    void deleteSport(Sport sport);

}
