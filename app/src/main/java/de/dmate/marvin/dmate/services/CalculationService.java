package de.dmate.marvin.dmate.services;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

public class CalculationService extends Service {

    private IBinder binder = new LocalBinder();

    private DataViewModel viewModel;

    private List<Daytime> daytimes;
    private List<Entry> entries;
    private List<Exercise> exercises;
    private List<Notification> notifications;
    private List<Observation> observations;
    private List<PlannedBasalInjection> plannedBasalInjections;
    private List<Sport> sports;
    private List<User> users;
    private User user;

    private Boolean daytimesLoaded = false;
    private Boolean entriesLoaded = false;
    private Boolean exercisesLoaded = false;
    private Boolean notificationsLoaded = false;
    private Boolean observationsLoaded = false;
    private Boolean plannedBasalInjectionsLoaded = false;
    private Boolean sportsLoaded = false;
    private Boolean usersLoaded = false;

    private Observer<List<Daytime>> obsDaytimes;
    private Observer<List<Entry>> obsEntries;
    private Observer<List<Exercise>> obsExercises;
    private Observer<List<Notification>> obsNotifications;
    private Observer<List<Observation>> obsObservations;
    private Observer<List<PlannedBasalInjection>> obsPlannedBasalInjections;
    private Observer<List<Sport>> obsSports;
    private Observer<List<User>> obsUsers;

    //empty default constructor
    public CalculationService() {

    }

    //Binder class definition
    public class LocalBinder extends Binder {
        public CalculationService getService() {
            return CalculationService.this;
        }
    }

    //onBind -> return custom binder interface
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //get viewModel and start observing LiveData for all tables
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DataViewModel.class);


        obsDaytimes = new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {

            }
        };
        viewModel.getDaytimes().observeForever(obsDaytimes);


        obsEntries = new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                //perform calculations with entries in here
            }
        };
        viewModel.getEntries().observeForever(obsEntries);


        obsExercises = new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {

            }
        };
        viewModel.getExercises().observeForever(obsExercises);


        obsNotifications = new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {

            }
        };
        viewModel.getNotifications().observeForever(obsNotifications);


        obsObservations = new Observer<List<Observation>>() {
            @Override
            public void onChanged(@Nullable List<Observation> observations) {

            }
        };
        viewModel.getObservations().observeForever(obsObservations);


        obsPlannedBasalInjections = new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {

            }
        };
        viewModel.getPlannedBasalInjections().observeForever(obsPlannedBasalInjections);


        obsSports = new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {

            }
        };
        viewModel.getSports().observeForever(obsSports);


        obsUsers = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {

            }
        };
        viewModel.getUsers().observeForever(obsUsers);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getDaytimes().removeObserver(obsDaytimes);
        viewModel.getEntries().removeObserver(obsEntries);
        viewModel.getExercises().removeObserver(obsExercises);
        viewModel.getNotifications().removeObserver(obsNotifications);
        viewModel.getObservations().removeObserver(obsObservations);
        viewModel.getPlannedBasalInjections().removeObserver(obsPlannedBasalInjections);
        viewModel.getSports().removeObserver(obsSports);
        viewModel.getUsers().removeObserver(obsUsers);
    }

    //getter
    public List<Daytime> getDaytimes() {
        return daytimes;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public List<PlannedBasalInjection> getPlannedBasalInjections() {
        return plannedBasalInjections;
    }

    public List<Sport> getSports() {
        return sports;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    public Boolean getDaytimesLoaded() {
        return daytimesLoaded;
    }

    public Boolean getEntriesLoaded() {
        return entriesLoaded;
    }

    public Boolean getExercisesLoaded() {
        return exercisesLoaded;
    }

    public Boolean getNotificationsLoaded() {
        return notificationsLoaded;
    }

    public Boolean getObservationsLoaded() {
        return observationsLoaded;
    }

    public Boolean getPlannedBasalInjectionsLoaded() {
        return plannedBasalInjectionsLoaded;
    }

    public Boolean getSportsLoaded() {
        return sportsLoaded;
    }

    public Boolean getUsersLoaded() {
        return usersLoaded;
    }
    //setter
    public void setDaytimes(List<Daytime> daytimes) {
        this.daytimes = daytimes;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public void setPlannedBasalInjections(List<PlannedBasalInjection> plannedBasalInjections) {
        this.plannedBasalInjections = plannedBasalInjections;
    }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDaytimesLoaded(Boolean daytimesLoaded) {
        this.daytimesLoaded = daytimesLoaded;
    }

    public void setEntriesLoaded(Boolean entriesLoaded) {
        this.entriesLoaded = entriesLoaded;
    }

    public void setExercisesLoaded(Boolean exercisesLoaded) {
        this.exercisesLoaded = exercisesLoaded;
    }

    public void setNotificationsLoaded(Boolean notificationsLoaded) {
        this.notificationsLoaded = notificationsLoaded;
    }

    public void setObservationsLoaded(Boolean observationsLoaded) {
        this.observationsLoaded = observationsLoaded;
    }

    public void setPlannedBasalInjectionsLoaded(Boolean plannedBasalInjectionsLoaded) {
        this.plannedBasalInjectionsLoaded = plannedBasalInjectionsLoaded;
    }

    public void setSportsLoaded(Boolean sportsLoaded) {
        this.sportsLoaded = sportsLoaded;
    }

    public void setUsersLoaded(Boolean usersLoaded) {
        this.usersLoaded = usersLoaded;
    }
}