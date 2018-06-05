package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Analysis;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface AnalysisDao {

    //get all analyses by userId
    @Query("SELECT * FROM analyses WHERE uIdF = :userId")
    LiveData<List<Analysis>> getAllAnalysesByUserId(Integer userId);

    //get analysis by Id
    @Query("SELECT * FROM analyses WHERE aId = :analysisId")
    Analysis getAnalysisById(Integer analysisId);

    @Insert(onConflict = REPLACE)
    void insertAnalysis(Analysis analysis);

    @Delete
    void deleteAnalysis(Analysis analysis);

}
