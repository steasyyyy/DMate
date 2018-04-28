package de.dmate.marvin.dmate.roomDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

//DAO = data access object
//defines methods for data access and manipulation in the db
//NEVER CALL METHODS DIRECTLY FROM THE UI THREAD! ALWAYS USE METHODS IN ENTRYLISTVIEWMODEL!
@Dao
@TypeConverters(RoomConverter.class)
public interface EntryDao {

    //when method gets called via EntryViewModel, the defined query is executed
    //Room understands insert, delete and update by itself (no need to define a query)

    //get a complete list of all entries ordered by dateTime
    @Query("SELECT * FROM entries ORDER BY timestamp DESC")
    LiveData<List<Entry>> getAllEntries();

    //get entry by ID
    @Query("SELECT * FROM entries WHERE eId = :eId")
    Entry getItemById(int eId);

    //get last entry of day
    @Query("SELECT * FROM entries WHERE timestamp = (SELECT MIN(timestamp) FROM entries)")
    Entry getLastEntryOfDay();

    //insert entry
    @Insert(onConflict = REPLACE)
    void insertEntryRoom(Entry entry);

    //insert multiple entries
    @Insert(onConflict = REPLACE)
    void insertEntries(List<Entry> entries);

    //delete entry by ID
    @Delete
    void deleteEntryRoom(Entry entry);

    //update entry by ID
    @Update
    void updateEntryRoom(Entry entry);


}
