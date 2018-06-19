package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.RoomConverter;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

//DAO = data access object
//defines methods for data access and manipulation in the db
//NEVER CALL METHODS DIRECTLY FROM THE UI THREAD! ALWAYS USE METHODS IN DATAVIEWMODEL!
@Dao
@TypeConverters(RoomConverter.class)
public interface EntryDao {

    //when method gets called via DataViewModel, the defined query is executed
    //Room understands insert, delete and update by itself (no need to define a query)

    //get a complete list of all entries by user ordered by dateTime
    @Query("SELECT * FROM entries ORDER BY timestamp DESC")
    LiveData<List<Entry>> getAllEntries();

    //get entry by ID
    @Query("SELECT * FROM entries WHERE eId = :eId")
    Entry getEntryById(Integer eId);

    //insert entry
    @Insert(onConflict = REPLACE)
    void insertEntry(Entry entry);

    //insert multiple entries
    @Insert(onConflict = REPLACE)
    void insertEntries(List<Entry> entries);

    //delete entry
    @Delete
    void deleteEntryRoom(Entry entry);

    //update entry
    @Update
    void updateEntryRoom(Entry entry);


}
