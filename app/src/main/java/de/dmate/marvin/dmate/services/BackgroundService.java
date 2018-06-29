package de.dmate.marvin.dmate.services;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;
import de.dmate.marvin.dmate.util.AlarmReceiver;
import de.dmate.marvin.dmate.util.Helper;
import de.dmate.marvin.dmate.util.NotificationScheduler;
import de.dmate.marvin.dmate.util.RunnableHelper;

public class BackgroundService extends Service {

    private IBinder binder = new LocalBinder();

    private DataViewModel viewModel;

    private List<Daytime> daytimes;
    private List<Entry> entries; //sometimes it is necessary to consider all entries though
    private List<Entry> entriesFromPastTwoWeeks; //NOTE: only entries from the past two weeks are considered as specified in the requirements
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

    private ExecutorService executor;

    public BackgroundService() {

    }

    //Binder class definition
    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
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

        executor = Executors.newSingleThreadExecutor();

        //get viewModel and start observing LiveData for all tables
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DataViewModel.class);

        obsDaytimes = new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                BackgroundService.this.daytimes = daytimes;
                BackgroundService.this.daytimesLoaded = true;

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
                    //update DaytimeID in all Entries (CHECK)
                    //trigger notification if some entries do not have a DaytimeID assigned (CHECK)
                    //update buFactorConsultingArithMean in Daytime (NOT TESTED YET)
                    //update reqBolusSimple in ALL ENTRIES (CHECK)
                    //update buFactorConsulting in ALL ENTRIES
                    //update reqBolusConsulting in ALL ENTRIES
                    //update more
                }
            }
        };
        viewModel.getDaytimes().observeForever(obsDaytimes);


        obsEntries = new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {

                List<Entry> entriesFromPastTwoWeeks = new ArrayList<>();
                long millisPerDay = 24*60*60*1000;
                long now = System.currentTimeMillis();
                long twoWeeksAgo = now - (14*millisPerDay);
                for (Entry e : entries) {
                    if (e.getTimestamp().getTime() >= twoWeeksAgo) {
                        entriesFromPastTwoWeeks.add(e);
                    }
                }

                BackgroundService.this.entries = entries;
                BackgroundService.this.entriesFromPastTwoWeeks = entriesFromPastTwoWeeks;
                BackgroundService.this.entriesLoaded = true;

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
                    //-> update bloodSugarArithMean in User (CHECK)
                    //-> -> update trend calculations for notification type 2 (Adjustment of basal insulin dose)
                    //-> -> update notifications
                    //-> check if blood sugar value exists
                    //-> -> update divergenceFromTarget in all entries (CHECK)
                    //-> check if bread units exist
                    //-> -> update reqBolusSimple (CHECK)
                    //-> check if the new entry counts as a result of an existing entry (IMPORTANT)
                    //-> -> update divergenceFromTarget in existing entry (CHECK BY DOING IT FOR ALL)
                    //-> -> update buFactorConsulting in existing entry
                    //-> -> update reqBolusConsulting in existing entry
                    //-> check if the new entry counts as an initial entry (has bread units and a bolus insulin injection) (IMPORTANT)
                    //-> -> update divergenceFromTarget in new entry
                    //-> -> update bolusCorrectionBS in new entry (CHECK)
                    //-> -> update bolusCorrectionSport in new entry (CHECK, watch out though that only entries are updated that could be matched to a daytime)
                    //-> -> update buFactorConsulting in new entry
                    //-> -> update reqBolusConsulting in new entry
                    //-> check if the new entry counts as an initial entry of an existing entry (has bread units, a bolus insulin injection and an existing entry that counts as a result)
                    //-> ->
                    //-> check exercises

                    //2 - check if an entry was deleted
                    //-> update bloodSugarArithMean in User
                    //-> -> update trend calculations for notification type 2 (Adjustment of basal insulin dose)
                    //-> -> update notifications
                    //-> check if the deleted entry had counted as a result of another entry
                    //-> -> update divergenceFromTarget in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> update buFactorConsulting in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> update reqBolusConsulting in initial entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> check exercises


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

                    //check if the basal insulin dose was updated
                    //-> update observations
                    //-> update notifications

                    //check if diseased status was updated
                    //-> update bloodSugarArithMean in User (not considering the updated entry)
                    //-> check if the updated entry counted as a result of an existing entry
                    //-> -> update buFactorConsulting in existing entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)
                    //-> -> update reqBolusConsulting in existing entry (NOTE: DO NOT DELETE THE VALUE, BUT RECALCULATE THE OLD ONE, THAT WAS CALCULATED TO GIVE A RECOMMENDATION TO THE USER)

                    //check if exercises were updated
                    //-> check if bread units an/or insulin dose exist
                    //-> -> update bolusCorrectionSport
                    //-> -> update reqBolusConsulting

                    //check if an observation should be started - conditions:
                    //-> no bolus insulin injection
                    //-> no bolus in action from earlier
                    //-> no observation already started for this time interval
                    //-> -> if an observation for this time interval exists, update it with the current information
                    //-> -> -> update trend of basal effect
                    //-> -> -> trigger notification if changed

                }
            }
        };
        viewModel.getEntries().observeForever(obsEntries);


        obsExercises = new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                BackgroundService.this.exercises = exercises;
                BackgroundService.this.exercisesLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();

                    //check if an exercise was added, deleted or updated
                    //-> check if bread units an/or insulin dose exist
                    //-> -> update bolusCorrectionSport
                    //-> -> update reqBolusConsulting
                }
            }
        };
        viewModel.getExercises().observeForever(obsExercises);


        obsNotifications = new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                BackgroundService.this.notifications = notifications;
                BackgroundService.this.notificationsLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();

                    //when notifications are added, check for their type and display them (either in-app or in the notification drawer)
                    //check if notification has been seen (Notification.hasBeenSeen was updated)
                    //-> remove notification from notification drawer (if necessary) and remove badge in-app
                }
            }
        };
        viewModel.getNotifications().observeForever(obsNotifications);


        obsObservations = new Observer<List<Observation>>() {
            @Override
            public void onChanged(@Nullable List<Observation> observations) {
                BackgroundService.this.observations = observations;
                BackgroundService.this.observationsLoaded = true;

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();

                    //check if observation was disrupted or is valid
                    //if valid -> update divergenceFromInitialValueArithMean in User
                    //if disrupted -> delete Observation
                }
            }
        };
        viewModel.getObservations().observeForever(obsObservations);


        obsPlannedBasalInjections = new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {
                BackgroundService.this.plannedBasalInjections = plannedBasalInjections;
                BackgroundService.this.plannedBasalInjectionsLoaded = true;

                for (PlannedBasalInjection pbi : plannedBasalInjections) {
                    Calendar c = Helper.getCalendarFromTimeString(pbi.getTimeOfDay());
                    NotificationScheduler.setReminder(BackgroundService.this, AlarmReceiver.class, c.get(Calendar.HOUR_OF_DAY) + 1, c.get(Calendar.MINUTE));
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

                //update triggers for notifications, especially type 1 - forgotten basal injections
            }
        };
        viewModel.getPlannedBasalInjections().observeForever(obsPlannedBasalInjections);


        obsSports = new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                BackgroundService.this.sports = sports;
                BackgroundService.this.sportsLoaded = true;

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

                //1 - check if new sport was added
                //2 - check if sport was deleted
                //-> delete all exercises that pointed to this sport
                //-> update exercises
                //-> -> check if bread units an/or insulin dose exist
                //-> -> -> update bolusCorrectionSport
                //-> -> -> update reqBolusConsulting
                //check if sport was updated
                //-> check if effectPerUnit was updated
                //-> -> update every entry.exercises where this sport was used
                //-> -> -> check if bread units an/or insulin dose exist
                //-> -> -> update bolusCorrectionSport
                //-> -> -> update reqBolusConsulting

            }
        };
        viewModel.getSports().observeForever(obsSports);


        obsUsers = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                try {
                    BackgroundService.this.user = users.get(0);
                    BackgroundService.this.usersLoaded = true;
                } catch (IndexOutOfBoundsException e) {
                    BackgroundService.this.user = new User();
                    viewModel.addUser(user);
                }

                NotificationScheduler.notificationsEnabled = user.getNotificationsEnabled();

                if (daytimesLoaded
                        && entriesLoaded
                        && exercisesLoaded
                        && notificationsLoaded
                        && observationsLoaded
                        && plannedBasalInjectionsLoaded
                        && sportsLoaded
                        && usersLoaded) {
                    performCalculations();

                    //check if duration of action of bolus insulin was updated
                    //-> update minAcceptanceTimeAsResult and maxAcceptanceTimeAsResult in User
                    //check if targetMin or targetMax was updated
                    //-> update ALL entries
                    //-> -> update divergenceFromTarget
                    //-> -> update bolusCorrectionBS
                    //-> -> update reqBolusConsulting
                }
            }
        };
        viewModel.getUsers().observeForever(obsUsers);
    }



    //conntrolling method of all calculations
    //gets called when onChanged of any LiveData is called and all other LiveData objects were initialized
    private void performCalculations() {

        executor.execute(RunnableHelper.getRunnableUpdateDaytimeIdsInAllEntries(viewModel, daytimes, entries));
        executor.execute(RunnableHelper.getRunnableTriggerNotificationIfDaytimesNotSetProperly(viewModel, entries, notifications));
        executor.execute(RunnableHelper.getRunnableTriggerPlannedBasalInjectionsNotSetWarning(viewModel, plannedBasalInjections, notifications));
        executor.execute(RunnableHelper.getRunnableUpdateReqBolusSimpleInAllEntries(viewModel, entries, daytimes));
        executor.execute(RunnableHelper.getRunnableUpdateBloodSugarArithMean(viewModel, user, entriesFromPastTwoWeeks));
        executor.execute(RunnableHelper.getRunnableUpdateDivergenceFromTargetInAllEntries(viewModel, user, entries, notifications));
        executor.execute(RunnableHelper.getRunnableUpdateBolusCorrectionBSForAllEntries(viewModel, user, entries, notifications, daytimes));
        executor.execute(RunnableHelper.getRunnableUpdateBolusCorrectionSportForAllEntries(viewModel, entries, notifications, daytimes, exercises, sports));
        executor.execute(RunnableHelper.getRunnableUpdateBuFactorRealInAllEntries(viewModel, entries));
        executor.execute(RunnableHelper.getRunnableUpdateBuFactorConsultingAfterResult(viewModel, user, entries, notifications));
        executor.execute(RunnableHelper.getRunnableUpdateBuFactorConsultingArithMeanInDaytimes(viewModel, entriesFromPastTwoWeeks, daytimes));
        executor.execute(RunnableHelper.getRunnableUpdateBuFactorConsulting(viewModel, entries, daytimes));
        executor.execute(RunnableHelper.getRunnableUpdateReqBolusConsultingInAllEntries(viewModel, entries, daytimes));
        executor.execute(RunnableHelper.getRunnableUpdateObservations(viewModel, user, observations, entries, notifications));
        executor.execute(RunnableHelper.getRunnableUpdateDivergenceFromStartValueArithMean(viewModel, user, entriesFromPastTwoWeeks, observations));
        executor.execute(RunnableHelper.getRunnableTriggerAdjustBasalNotification(viewModel, user, notifications));
        executor.execute(RunnableHelper.getRunnableRemoveObservationDuplicates(viewModel, observations));
        executor.execute(RunnableHelper.getRunnableRemoveNotificationDuplicates(viewModel, notifications));
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
    public IBinder getBinder() {
        return binder;
    }

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

    public Observer<List<Daytime>> getObsDaytimes() {
        return obsDaytimes;
    }

    public Observer<List<Entry>> getObsEntries() {
        return obsEntries;
    }

    public Observer<List<Exercise>> getObsExercises() {
        return obsExercises;
    }

    public Observer<List<Notification>> getObsNotifications() {
        return obsNotifications;
    }

    public Observer<List<Observation>> getObsObservations() {
        return obsObservations;
    }

    public Observer<List<PlannedBasalInjection>> getObsPlannedBasalInjections() {
        return obsPlannedBasalInjections;
    }

    public Observer<List<Sport>> getObsSports() {
        return obsSports;
    }

    public Observer<List<User>> getObsUsers() {
        return obsUsers;
    }

    //setter
    public void setBinder(IBinder binder) {
        this.binder = binder;
    }

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

    public void setObsDaytimes(Observer<List<Daytime>> obsDaytimes) {
        this.obsDaytimes = obsDaytimes;
    }

    public void setObsEntries(Observer<List<Entry>> obsEntries) {
        this.obsEntries = obsEntries;
    }

    public void setObsExercises(Observer<List<Exercise>> obsExercises) {
        this.obsExercises = obsExercises;
    }

    public void setObsNotifications(Observer<List<Notification>> obsNotifications) {
        this.obsNotifications = obsNotifications;
    }

    public void setObsObservations(Observer<List<Observation>> obsObservations) {
        this.obsObservations = obsObservations;
    }

    public void setObsPlannedBasalInjections(Observer<List<PlannedBasalInjection>> obsPlannedBasalInjections) {
        this.obsPlannedBasalInjections = obsPlannedBasalInjections;
    }

    public void setObsSports(Observer<List<Sport>> obsSports) {
        this.obsSports = obsSports;
    }

    public void setObsUsers(Observer<List<User>> obsUsers) {
        this.obsUsers = obsUsers;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}