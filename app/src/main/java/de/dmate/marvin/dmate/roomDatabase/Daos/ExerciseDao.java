package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ExerciseDao {

//    //get all exercises by entryId
//    @Query("SELECT * FROM exercises WHERE eIdF = :entryId")
//    LiveData<List<Daytime>> getAllExercisesByEntryId(Integer entryId);

    //get all exercises
    @Query("SELECT * FROM exercises")
    LiveData<List<Exercise>> getAllExercises();

    //get exercise by id
    @Query("SELECT * FROM exercises WHERE exId = :exId")
    Exercise getExerciseById(Integer exId);

    @Insert(onConflict = REPLACE)
    void insertExercise(Exercise exercise);

    @Delete
    void deleteExercise(Exercise exercise);

}
