package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PlannedBasalInjectionDao {

    //get all planned basal injections by userId
    @Query("SELECT * FROM plannedBasalInjections WHERE uIdF = :userId")
    LiveData<List<PlannedBasalInjection>> getAllPlannedBasalInjectionsByUserId(Integer userId);

    //get single planned basal injection by pbiId
    @Query("SELECT * FROM plannedBasalInjections WHERE pbiId = :pbiId")
    PlannedBasalInjection getPlannedBasalInjectionById(Integer pbiId);

    @Insert(onConflict = REPLACE)
    void insertPlannedBasalInjection(PlannedBasalInjection plannedBasalInjection);

    @Delete
    void deletePlannedBasalInjection(PlannedBasalInjection plannedBasalInjection);

}
