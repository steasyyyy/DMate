package de.dmate.marvin.dmate.roomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import de.dmate.marvin.dmate.roomDatabase.Daos.DaytimeDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.EntryDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.ExerciseDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.NotificationDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.ObservationDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.PlannedBasalInjectionDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.SportDao;
import de.dmate.marvin.dmate.roomDatabase.Daos.UserDao;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

//creation of the database
//entities -> tables of relational db
//NEVER CALL METHODS VIA APPDATABASE.ENTRYDAO.x DIRECTLY FROM THE UI THREAD
//USE ENTRYVIEWMODEL TO MANIPULATE DATA (ADD, REMOVE, UPDATE)
@Database(
        entities = {
                Entry.class,
                User.class,
                PlannedBasalInjection.class,
                Daytime.class,
                Sport.class,
                Exercise.class,
                Notification.class,
                Observation.class},
        version = 9)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "dmate_database").build();
        }
        return INSTANCE;
    }

    public abstract DaytimeDao daytimeDao();
    public abstract EntryDao entryDao();
    public abstract ExerciseDao exerciseDao();
    public abstract NotificationDao notificationDao();
    public abstract ObservationDao observationDao();
    public abstract PlannedBasalInjectionDao plannedBasalInjectionDao();
    public abstract SportDao sportDao();
    public abstract UserDao userDao();

}
