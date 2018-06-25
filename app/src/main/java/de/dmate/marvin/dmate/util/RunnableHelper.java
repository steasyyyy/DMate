package de.dmate.marvin.dmate.util;

import java.sql.Timestamp;
import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

public class RunnableHelper {

    public static Runnable getRunnableUpdateDaytimeIdsInAllEntries(DataViewModel viewModel, List<Daytime> daytimes, List<Entry> entries) {
        return new UpdateDaytimeIdsInAllEntries(viewModel, daytimes, entries);
    }//uses:
    private static class UpdateDaytimeIdsInAllEntries implements Runnable {

        private final List<Entry> entries;
        private final List<Daytime> daytimes;
        private final DataViewModel viewModel;

        UpdateDaytimeIdsInAllEntries(DataViewModel viewModel, List<Daytime> daytimes, List<Entry> entries) {
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

    public static Runnable getRunnableTriggerNotificationIfDaytimesNotSetProperly(DataViewModel viewModel, List<Entry> entries, List<Notification> notifications) {
        return new TriggerNotificationIfDaytimesNotSetProperly(viewModel, entries, notifications);
    }
    private static class TriggerNotificationIfDaytimesNotSetProperly implements Runnable {

        private final List<Entry> entries;
        private final DataViewModel viewModel;
        private final List<Notification> notifications;

        TriggerNotificationIfDaytimesNotSetProperly(DataViewModel viewModel, List<Entry> entries, List<Notification> notifications) {
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

    public static Runnable getRunnableUpdateBuFactorConsultingArithMeanInDaytimes(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
        return new UpdateBuFactorConsultingArithMeanInDaytimes(viewModel, entries, daytimes);
    }
    private static class UpdateBuFactorConsultingArithMeanInDaytimes implements Runnable {

        private final List<Entry> entries;
        private final List<Daytime> daytimes;
        private final DataViewModel viewModel;

        UpdateBuFactorConsultingArithMeanInDaytimes(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.daytimes = daytimes;
        }

        @Override
        public void run() {
            for (Daytime d : daytimes) {
                float amount = 0f;
                int divideBy = 0;
                float result;
                for (Entry e : entries) {
                    if (e.getdIdF() != null && e.getReliable()) {
                        if (e.getdIdF().equals(d.getdId())) {
                            if (e.getBuFactorConsulting() != null) {
                                amount += e.getBuFactorConsulting();
                                divideBy++;
                            }
                        }
                    }
                }
                if (amount != 0f && divideBy != 0) {
                    result = amount/divideBy;
                } else break;

                if (d.getBuFactorConsultingArithMean() != null) {
                    if (!(d.getBuFactorConsultingArithMean().equals(result))) {
                        d.setBuFactorConsultingArithMean(result);
                        viewModel.addDaytime(d);
                        System.out.println("DAYTIME UPDATED (BU FACTOR CONSULTING)");
                    }
                } else {
                    d.setBuFactorConsultingArithMean(result);
                    viewModel.addDaytime(d);
                    System.out.println("DAYTIME UPDATED (BU FACTOR CONSULTING)");
                }
            }
        }
    }

    public static Runnable getRunnableUpdateReqBolusSimpleInAllEntries(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
        return new UpdateReqBolusSimpleInAllEntries(viewModel, entries, daytimes);
    }
    private static class UpdateReqBolusSimpleInAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;
        private final List<Daytime> daytimes;

        UpdateReqBolusSimpleInAllEntries(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.daytimes = daytimes;
        }

        @Override
        public void run() {
            for (Entry e : entries) {
                if (e.getdIdF() != null && e.getBreadUnit() != null) {
                    for (Daytime d : daytimes) {
                        if (e.getdIdF().equals(d.getdId())) {
                            float result = e.getBreadUnit()*d.getBuFactor();
                            if (!(e.getReqBolusSimple().equals(result))) {
                                e.setReqBolusSimple(result);
                                viewModel.addEntry(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Runnable getRunnableUpdateBloodSugarArithMean(DataViewModel viewModel, User user, List<Entry> entries) {
        return new UpdateBloodSugarArithMean(viewModel, user, entries);
    }
    private static class UpdateBloodSugarArithMean implements Runnable {

        private final DataViewModel viewModel;
        private final User user;
        private final List<Entry> entries;

        UpdateBloodSugarArithMean(DataViewModel viewModel, User user, List<Entry> entries) {
            this.viewModel = viewModel;
            this.user = user;
            this.entries = entries;
        }

        @Override
        public void run() {
            float amount = 0f;
            int divideBy = 0;
            float result = 0f;

            for (Entry e : entries) {
                if (e.getBloodSugar() != null /* && e.getReliable()*/) { //TODO check reliability
                    amount += e.getBloodSugar();
                    divideBy++;
                }
            }

            if (amount != 0f && divideBy != 0) {
                result = amount/divideBy;
            }

            if (result > 0) {
                if (user.getBloodsugarArithMean() != null) {
                    if (!user.getBloodsugarArithMean().equals(result)) {
                        user.setBloodsugarArithMean(result);
                        viewModel.addUser(user);
                    }
                } else {
                    user.setBloodsugarArithMean(result);
                    viewModel.addUser(user);
                }
            }
            System.out.println("BLOOD SUGAR ARITH MEAN: " + user.getBloodsugarArithMean());
        }
    }
}
