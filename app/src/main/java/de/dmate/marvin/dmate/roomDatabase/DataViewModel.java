package de.dmate.marvin.dmate.roomDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

//ACCESS AND MANIPULATE DATA IN THE DATABASE FROM HERE
//functions are available for inserting into db, deleting from db and getting a complete list of entries to certain tables as LiveData<List<T>>
public class DataViewModel extends AndroidViewModel {

    private final LiveData<List<Daytime>> daytimes;
    private final LiveData<List<Entry>> entries;
    private final LiveData<List<Exercise>> exercises;
    private final LiveData<List<Notification>> notifications;
    private final LiveData<List<Observation>> observations;
    private final LiveData<List<PlannedBasalInjection>> plannedBasalInjections;
    private final LiveData<List<Sport>> sports;
    private final LiveData<List<User>> users;

    private AppDatabase appDatabase;

    public DataViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        daytimes = appDatabase.daytimeDao().getAllDaytimes();
        entries = appDatabase.entryDao().getAllEntries();
        exercises = appDatabase.exerciseDao().getAllExercises();
        notifications = appDatabase.notificationDao().getAllNotifications();
        observations = appDatabase.observationDao().getAllObservations();
        plannedBasalInjections = appDatabase.plannedBasalInjectionDao().getAllPlannedBasalInjections();
        sports = appDatabase.sportDao().getAllSports();
        users = appDatabase.userDao().getAllUsers();
    }

    //DAYTIMES
    //get all daytimes
    public LiveData<List<Daytime>> getDaytimes() {
        return daytimes;
    }

    //add a daytime to the database
    public void addDaytime(Daytime daytime) {
        new addDaytimeAsyncTask(appDatabase).execute(daytime);
    }

    //delete a daytime from the database
    public void deleteDaytime(Daytime daytime) {
        new deleteDaytimeAsyncTask(appDatabase).execute(daytime);
    }


    //ENTRIES
    //get all entries
    public LiveData<List<Entry>> getEntries() {
        return entries;
    }

    //add an entry to the database
    public void addEntry(Entry entry) {
        new addEntryAsyncTask(appDatabase).execute(entry);
    }

    public void addEntryWithExercises(Entry entry, List<Exercise> exercises) {
        new addEntryWithExercisesAsyncTask(appDatabase, entry, exercises).execute();
    }

    public void updateEntryReference(Integer eId, Integer exId) {
        new updateEntryReferenceAsyncTask(appDatabase, eId, exId).execute();
    }

    //delete an entry from the database
    public void deleteEntry(Entry entry) {
        new deleteEntryAsyncTask(appDatabase).execute(entry);
    }


    //EXERCISES
    //get all exercises
    public LiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    //add an exercise to the database
    public void addExercise(Exercise exercise) {
        new addExerciseAsyncTask(appDatabase).execute(exercise);
    }

    public void deleteExercise(Exercise exercise) {
        new deleteExerciseAsyncTask(appDatabase).execute(exercise);
    }


    //NOTIFICATIONS
    //get all notifications
    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    //add a notification to the database
    public void addNotification(Notification notification) {
        new addNotificationAsyncTask(appDatabase).execute(notification);
    }

    //delete a notification from the database
    public void deleteNotification(Notification notification) {
        new deleteNotificationAsyncTask(appDatabase).execute(notification);
    }


    //OBSERVATIONS
    //get all observations
    public LiveData<List<Observation>> getObservations() {
        return observations;
    }

    //add an observation to the database
    public void addObservation(Observation observation) {
        new addObservationAsyncTask(appDatabase).execute(observation);
    }

    //delete an observation from the database
    public void deleteObservation(Observation observation) {
        new deleteObservationAsyncTask(appDatabase).execute(observation);
    }


    //PLANNEDBASALINJECTIONS
    //get all planned basal injections
    public LiveData<List<PlannedBasalInjection>> getPlannedBasalInjections() {
        return plannedBasalInjections;
    }

    //add a planned basal injection to the database
    public void addPlannedBasalInjection(PlannedBasalInjection plannedBasalInjection) {
        new addPlannedBasalInjectionAsyncTask(appDatabase).execute(plannedBasalInjection);
    }

    //delete a planned basal injection from the database
    public void deletePlannedBasalInjection(PlannedBasalInjection plannedBasalInjection) {
        new deletePlannedBasalInjectionAsyncTask(appDatabase).execute(plannedBasalInjection);
    }


    //SPORTS
    //get all sports
    public LiveData<List<Sport>> getSports() {
        return sports;
    }

    //add a sport to the database
    public void addSport(Sport sport) {
        new addSportAsyncTask(appDatabase).execute(sport);
    }

    //delete a sport from the database
    public void deleteSport(Sport sport) {
        new deleteSportAsyncTask(appDatabase).execute(sport);
    }


    //USERS
    //get all users
    public LiveData<List<User>> getUsers() {
        return users;
    }

    //add a user to the database
    public void addUser(User user) {
        new addUserAsyncTask(appDatabase).execute(user);
    }

    //delete a user from the database
    public void deleteUser(User user) {
        new deleteUserAsyncTask(appDatabase).execute(user);
    }

    //delete all users
    public void deleteAllUsers() {
        new deleteAllUsersAsyncTask(appDatabase).execute();
    }



    //ASYNCTASKS
    //AsyncTasks are instanciated and executed for every transaction defined above
    //DAYTIMES
    private static class addDaytimeAsyncTask extends AsyncTask<Daytime, Void, Void> {
        private AppDatabase db;

        addDaytimeAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Daytime... params) {
            db.daytimeDao().insertDaytime(params[0]);
            return null;
        }
    }

    private static class deleteDaytimeAsyncTask extends AsyncTask<Daytime, Void, Void> {
        private AppDatabase db;

        deleteDaytimeAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Daytime... params) {
            db.daytimeDao().deleteDaytime(params[0]);
            return null;
        }
    }


    //ENTRIES
    private static class addEntryAsyncTask extends AsyncTask<Entry, Void, Void> {
        private AppDatabase db;

        addEntryAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Entry... params) {
            db.entryDao().insertEntry(params[0]);
            return null;
        }
    }

    private static class updateEntryReferenceAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;
        private Integer eId;
        private Integer exId;

        updateEntryReferenceAsyncTask(AppDatabase db, Integer eId, Integer exId) {
            this.db = db;
            this.eId = eId;
            this.exId = exId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.exerciseDao().updateEntryReference(eId, exId);
            return null;
        }
    }

    private static class addEntryWithExercisesAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;
        private Entry entry;
        private List<Exercise> exercises;

        addEntryWithExercisesAsyncTask(AppDatabase db, Entry entry, List<Exercise> exercises) {
            this.db = db;
            this.entry = entry;
            this.exercises = exercises;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            long rowId = db.entryDao().insertEntry(entry);

            //if exercises exist, write them to the database and set the foreign key to the EntryID of the entry that was just inserted
            if (exercises.size() > 0) {
                for (Exercise ex : exercises) {
                    ex.seteIdF((int) rowId);
                    db.exerciseDao().insertExercise(ex);
                }
            }
            return null;
        }
    }

    private static class deleteEntryAsyncTask extends AsyncTask<Entry, Void, Void> {
        private AppDatabase db;

        deleteEntryAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(final Entry... params) {
            db.entryDao().deleteEntryRoom(params[0]);
            return null;
        }
    }


    //EXERCISES
    private static class addExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private AppDatabase db;

        addExerciseAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Exercise... params) {
            db.exerciseDao().insertExercise(params[0]);
            return null;
        }
    }

    private static class deleteExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private AppDatabase db;

        deleteExerciseAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Exercise... params) {
            db.exerciseDao().deleteExercise(params[0]);
            return null;
        }
    }


    //NOTIFICATIONS
    private static class addNotificationAsyncTask extends AsyncTask<Notification, Void, Void> {
        private AppDatabase db;

        addNotificationAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Notification... params) {
            db.notificationDao().insertNotification(params[0]);
            return null;
        }
    }

    private static class deleteNotificationAsyncTask extends AsyncTask<Notification, Void, Void> {
        private AppDatabase db;

        deleteNotificationAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Notification... params) {
            db.notificationDao().deleteNotification(params[0]);
            return null;
        }
    }


    //OBSERVATIONS
    private static class addObservationAsyncTask extends AsyncTask<Observation, Void, Void> {
        private AppDatabase db;

        addObservationAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Observation... params) {
            db.observationDao().insertObservation(params[0]);
            return null;
        }
    }

    private static class deleteObservationAsyncTask extends AsyncTask<Observation, Void, Void> {
        private AppDatabase db;

        deleteObservationAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Observation... params) {
            db.observationDao().deleteObservation(params[0]);
            return null;
        }
    }


    //PLANNEDBASALINJECTIONS
    private static class addPlannedBasalInjectionAsyncTask extends AsyncTask<PlannedBasalInjection, Void, Void> {
        private AppDatabase db;

        addPlannedBasalInjectionAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(PlannedBasalInjection... params) {
            db.plannedBasalInjectionDao().insertPlannedBasalInjection(params[0]);
            return null;
        }
    }

    private static class deletePlannedBasalInjectionAsyncTask extends AsyncTask<PlannedBasalInjection, Void, Void> {
        private AppDatabase db;

        deletePlannedBasalInjectionAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(PlannedBasalInjection... params) {
            db.plannedBasalInjectionDao().deletePlannedBasalInjection(params[0]);
            return null;
        }
    }


    //SPORTS
    private static class addSportAsyncTask extends AsyncTask<Sport, Void, Void> {
        private AppDatabase db;

        addSportAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Sport... params) {
            db.sportDao().insertSport(params[0]);
            return null;
        }
    }

    private static class deleteSportAsyncTask extends AsyncTask<Sport, Void, Void> {
        private AppDatabase db;

        deleteSportAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Sport... params) {
            db.sportDao().deleteSport(params[0]);
            return null;
        }
    }


    //USERS
    private static class addUserAsyncTask extends AsyncTask<User, Void, Void> {
        private AppDatabase db;

        addUserAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(User... params) {
            db.userDao().insertUser(params[0]);
            return null;
        }
    }

    private static class deleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private AppDatabase db;

        deleteUserAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(User... params) {
            db.userDao().deleteUser(params[0]);
            return null;
        }
    }

    private static class deleteAllUsersAsyncTask extends AsyncTask<User, Void, Void> {
        private AppDatabase db;

        deleteAllUsersAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(User... params) {
            db.userDao().deleteAllUsers();
            return null;
        }
    }

}
