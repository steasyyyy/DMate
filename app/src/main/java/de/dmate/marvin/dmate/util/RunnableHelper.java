package de.dmate.marvin.dmate.util;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.services.CalculationService;

public class RunnableHelper {

    private static RunnableHelper instance;

    private RunnableHelper() {

    }

    public static RunnableHelper getInstance() {
        if (RunnableHelper.instance == null) {
            RunnableHelper.instance = new RunnableHelper();
        }
        return RunnableHelper.instance;
    }

    public Runnable getRunnableUpdateDaytimeIdsInAllEntries(DataViewModel viewModel, List<Daytime> daytimes, List<Entry> entries) {
        return new UpdateDaytimeIdsInAllEntries(viewModel, daytimes, entries);
    }//uses:
    private class UpdateDaytimeIdsInAllEntries implements Runnable {

        private final List<Entry> entries;
        private final List<Daytime> daytimes;
        private final DataViewModel viewModel;

        public UpdateDaytimeIdsInAllEntries(DataViewModel viewModel, List<Daytime> daytimes, List<Entry> entries) {
            this.viewModel = viewModel;
            this.daytimes = daytimes;
            this.entries = entries;
        }
        @Override
        public void run() {
            //iterate through daytimes and entries and set every entry's DaytimeID to the daytimes ID if
            //the entry's timestamp is between daytimeStart and daytimeEnd
            for (Daytime d : daytimes) {
                Timestamp daytimeStart = Helper.getTimestampFromTimeString(d.getDaytimeStart());
                Timestamp daytimeEnd = Helper.getTimestampFromTimeString(d.getDaytimeEnd());

                System.out.println("Daytime " + d.getdId() + " starts at " + daytimeStart.toString() + " and ends at " + daytimeEnd.toString());

                for (Entry e : entries) {
                    String entryTimeString = Helper.formatMillisToTimeString(e.getTimestamp().getTime());
                    Timestamp cleanedTimestamp = Helper.getTimestampFromTimeString(entryTimeString);

                    if (cleanedTimestamp.after(daytimeStart) && cleanedTimestamp.before(daytimeEnd)) {
                        if (e.getdIdF() == null) {
                            e.setdIdF(d.getdId());
                            viewModel.addEntry(e);
                            System.out.println("Entry " + e.geteId() + " was assigned to daytime with ID : " + d.getdId());
                        }
                        if (!(e.getdIdF().equals(d.getdId()))) {
                            e.setdIdF(d.getdId());
                            viewModel.addEntry(e);
                            System.out.println("Entry " + e.geteId() + " was assigned to daytime with ID : " + d.getdId());
                        }
                    }
                }
            }
        }
    }

    public Runnable getRunnableTriggerNotificationIfDaytimesNotSetProperly(DataViewModel viewModel, List<Entry> entries, List<Notification> notifications) {
        return new TriggerNotificationIfDaytimesNotSetProperly(viewModel, entries, notifications);
    }
    private class TriggerNotificationIfDaytimesNotSetProperly implements Runnable {

        private final List<Entry> entries;
        private final DataViewModel viewModel;
        private final List<Notification> notifications;

        public TriggerNotificationIfDaytimesNotSetProperly(DataViewModel viewModel, List<Entry> entries, List<Notification> notifications) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.notifications = notifications;
        }
        @Override
        public void run() {
            boolean nAlreadyExisting = false;
            for (Notification n : notifications) {
                if (n.getNotificationType().equals(Notification.DAYTIME_WARNING)) {
                    nAlreadyExisting = true;
                    break;
                }
            }
            //if there are entries that do not have a DaytimeID and this notification does not already exist, trigger a new one
            for (Entry e : entries) {
                if (e.getdIdF() == null && !nAlreadyExisting) {
                    Notification newN = new Notification();
                    newN.setNotificationType(Notification.DAYTIME_WARNING);
                    newN.setMessage(Notification.MESSAGE_DAYTIME_WARNING);
                    newN.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    viewModel.addNotification(newN);
                    System.out.println("NEW NOTIFICATION ADDED (DAYTIME WARNING)");
                    break;
                }
            }
        }
    }
}
