package de.dmate.marvin.dmate.services;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private ThreadPoolExecutor executor;
    private LinkedBlockingQueue<Runnable> queue;

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

        //initialize ThreadPoolExecutor
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        queue = new LinkedBlockingQueue<Runnable>();
        executor = new ThreadPoolExecutor(corePoolSize, corePoolSize*2, 1, TimeUnit.MINUTES, queue);



        //get viewModel and start observing LiveData for all tables
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DataViewModel.class);

        obsDaytimes = new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                CalculationService.this.daytimes = daytimes;
                CalculationService.this.daytimesLoaded = true;


                //check if every list is loaded before performing calculations
                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                    //update DaytimeID in all Entries
                    //update bread unit consulting arith mean in Daytime
                    //update more
                }
            }
        };
        viewModel.getDaytimes().observeForever(obsDaytimes);


        obsEntries = new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                CalculationService.this.entries = entries;
                CalculationService.this.entriesLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();

                    //1 - check if a new entry was added
                    //-> update bloodSugarArithMean in User
                    //-> -> update trend calculations for notification type 2 (Adjustment of basal insulin dose)
                    //-> -> update notifications
                    //-> check if blood sugar value exists
                    //-> -> update divergenceFromTarget in new entry
                    //-> check if bread units exist
                    //-> -> update reqBolusSimple
                    //-> check if the new entry counts as a result of an existing entry
                    //-> -> update divergenceFromTarget in existing entry
                    //-> -> update buFactorConsulting in existing entry
                    //-> -> update reqBolusConsulting in existing entry
                    //-> check if the new entry counts as an initial entry (has bread units and a bolus insulin injection)
                    //-> -> update divergenceFromTarget in new entry
                    //-> -> update bolusCorrectionBS in new entry
                    //-> -> update bolusCorrectionSport in new entry
                    //-> -> update buFactorConsulting in new entry
                    //-> -> update reqBolusConsulting in new entry
                    //-> check if the new entry counts as an initial entry of an existing entry (has bread units, a bolus insulin injection and an existing entry that counts as a result)
                    //-> ->

                    //2 - check if an entry was deleted
                    //-> update bloodSugarArithMean in User
                    //-> -> update trend calculations for notification type 2 (Adjustment of basal insulin dose)
                    //-> -> update notifications
                    //-> check if the deleted entry had counted as a result of another entry
                    //-> -> update divergenceFromTarget in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> update buFactorConsulting in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> update reqBolusConsulting in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)


                    //3 - check if an entry was updated

                    //check if the timestamp was updated
                    //-> check if the updated entry had counted as a result of an existing entry
                    //-> -> check if the updated entry still counts as a result of the same existing entry
                    //-> -> -> if NOT:
                    //-> -> -> -> update divergenceFromTarget in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> -> -> update buFactorConsulting in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> -> -> update reqBolusConsulting in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> check if the updated entry had counted as an initial entry
                    //-> -> check if the result entry of the updated entry still counts as a result entry
                    //-> -> -> if NOT:
                    //-> -> -> -> update divergenceFromTarget in updated entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> -> -> update buFactorConsulting in updated entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> -> -> update reqBolusConsulting in updated entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)

                    //check if the blood sugar value was updated
                    //-> update bloodSugarArithMean in User
                    //-> -> update trend calculations for notification type 2 (Adjustment of basal insulin dose)
                    //-> -> update notifications
                    //-> update divergenceFromTarget in new entry
                    //-> -> update bolusCorrectionBS
                    //-> check if the updated entry counted as a result of an existing entry
                    //-> -> update buFactorReal in the initial entry
                    //-> -> update divergenceFromTarget in the initial entry
                    //-> -> update buFactorConsulting in the initial entry
                    //-> -> update reqBolusConsulting in the initial entry
                    //-> check if the updated entry counted as an initial entry
                    //-> -> update buFactorReal in the updated entry
                    //-> -> update divergenceFromTarget in the updated entry
                    //-> -> update buFactorConsulting in the updated entry
                    //-> -> update reqBolusConsulting in the updated entry
                    //-> -> update bolusCorrectionBS
                    //-> -> update bolusCorrectionSport

                    //check if bread units were updated
                    //-> update reqBolusSimple
                    //-> check if the updated entry counted as a result of an existing entry
                    //-> -> update buFactorReal in the updated entry
                    //-> -> update divergenceFromTarget in existing entry
                    //-> -> updatebuFactorConsulting in existing entry
                    //-> -> update reqBolusConsulting in existing entry
                    //-> check if the updated entry counted as an initial entry
                    //-> -> update buFactorReal in the updated entry
                    //-> -> update divergenceFromTarget in the updated entry
                    //-> -> update buFactorConsulting in the updated entry
                    //-> -> update reqBolusConsulting in the updated entry

                    //check if the bolus insulin dose was updated
                    //-> check if the updated entry counted as a result of an existing entry
                    //-> ->
                    //-> check if the updated entry counted as an initial entry
                    //-> -> update buFactorReal


                }
            }
        };
        viewModel.getEntries().observeForever(obsEntries);


        obsExercises = new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                CalculationService.this.exercises = exercises;
                CalculationService.this.exercisesLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                }
            }
        };
        viewModel.getExercises().observeForever(obsExercises);


        obsNotifications = new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                CalculationService.this.notifications = notifications;
                CalculationService.this.notificationsLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                }
            }
        };
        viewModel.getNotifications().observeForever(obsNotifications);


        obsObservations = new Observer<List<Observation>>() {
            @Override
            public void onChanged(@Nullable List<Observation> observations) {
                CalculationService.this.observations = observations;
                CalculationService.this.observationsLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                }
            }
        };
        viewModel.getObservations().observeForever(obsObservations);


        obsPlannedBasalInjections = new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {
                CalculationService.this.plannedBasalInjections = plannedBasalInjections;
                CalculationService.this.plannedBasalInjectionsLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                }
            }
        };
        viewModel.getPlannedBasalInjections().observeForever(obsPlannedBasalInjections);


        obsSports = new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                CalculationService.this.sports = sports;
                CalculationService.this.sportsLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                }
            }
        };
        viewModel.getSports().observeForever(obsSports);


        obsUsers = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                try {
                    CalculationService.this.user = users.get(0);
                    CalculationService.this.usersLoaded = true;
                } catch (IndexOutOfBoundsException e) {
                    CalculationService.this.user = new User();
                    viewModel.addUser(user);
                    Toast toast = Toast.makeText(getApplicationContext(), "Created new user", Toast.LENGTH_LONG);
                    toast.show();
                }

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();
                }
            }
        };
        viewModel.getUsers().observeForever(obsUsers);
    }

    //conntrolling method of all calculations
    //gets called when onChanged of any LiveData is called and all other
    private void performCalculations() {

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