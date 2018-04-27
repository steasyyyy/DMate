package de.dmate.marvin.dmate.roomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

//creation of the database
//entities -> tables of relational db
//NEVER CALL METHODS VIA APPDATABASE.ENTRYDAO.x DIRECTLY FROM THE UI THREAD
//USE ENTRYLISTVIEWMODEL TO MANIPULATE DATA (ADD, REMOVE, UPDATE)
@Database(entities = {Entry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "entries_db").build();
        }
        return INSTANCE;
    }

    public abstract EntryDao entryRoomDao();

}
