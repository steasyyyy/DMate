package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ObservationDao {

    //get all observations
    @Query("SELECT * FROM observations")
    LiveData<List<Observation>> getAllObservations();

    @Query("SELECT * FROM observations WHERE oId = :observationId")
    Observation getObservationById(Integer observationId);

    @Insert(onConflict = REPLACE)
    void insertObservation(Observation observation);

    @Delete
    void deleteObservation(Observation observation);

}
