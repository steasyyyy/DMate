package de.dmate.marvin.dmate.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
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
                    Float result = 0f;
                    for (Daytime d : daytimes) {
                        if (e.getdIdF().equals(d.getdId())) {
                            result = e.getBreadUnit()*d.getBuFactor();
                            if (!(result.equals(0f))) {
                                if (e.getReqBolusSimple() == null) {
                                    e.setReqBolusSimple(result);
                                    viewModel.addEntry(e);
                                } else {
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
                if (e.getBloodSugar() != null && e.getReliable()) {
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

    public static Runnable getRunnableUpdateDivergenceFromTargetInAllEntries(DataViewModel viewModel, User user, List<Entry> entries, List<Notification> notifications) {
        return new UpdateDivergenceFromTargetInAllEntries(viewModel, user, entries, notifications);
    }
    private static class UpdateDivergenceFromTargetInAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final User user;
        private final List<Entry> entries;
        private final List<Notification> notifications;

        UpdateDivergenceFromTargetInAllEntries(DataViewModel viewModel, User user, List<Entry> entries, List<Notification> notifications) {
            this.viewModel = viewModel;
            this.user = user;
            this.entries = entries;
            this.notifications = notifications;
        }

        @Override
        public void run() {
            Float targetMin = user.getTargetMin();
            Float targetMax = user.getTargetMax();

            Boolean alreadyExisting = false;

            if (targetMin == null || targetMax == null) {
                //trigger Notification (Target not set)
                for (Notification n : notifications) {
                    if (n.getNotificationType().equals(Notification.TARGET_NOT_SET_WARNING)) {
                        alreadyExisting = true;
                    }
                }
                if (!alreadyExisting) {
                    Notification n = new Notification();
                    n.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    n.setNotificationType(Notification.TARGET_NOT_SET_WARNING);
                    n.setMessage(Notification.MESSAGE_TARGET_NOT_SET_WARNING);
                    viewModel.addNotification(n);
                    System.out.println("NEW NOTIFICATION ADDED (TARGET NOT SET)");
                }
            }
            if (targetMin != null && targetMax != null) {
                for (Entry e : entries) {
                    if (e.getBloodSugar() != null) {
                        Float divergenceFromTarget;
                        if (e.getBloodSugar() < targetMin) {
                            divergenceFromTarget = e.getBloodSugar() - targetMin;
                            if (e.getDivergenceFromTarget() == null) {
                                e.setDivergenceFromTarget(divergenceFromTarget);
                                viewModel.addEntry(e);
                            } else {
                                if (!(e.getDivergenceFromTarget().equals(divergenceFromTarget))) {
                                    e.setDivergenceFromTarget(divergenceFromTarget);
                                    viewModel.addEntry(e);
                                }
                            }
                        }
                        if (e.getBloodSugar() > targetMax) {
                            divergenceFromTarget = e.getBloodSugar() - targetMax;
                            if (e.getDivergenceFromTarget() == null) {
                                e.setDivergenceFromTarget(divergenceFromTarget);
                                viewModel.addEntry(e);
                            } else {
                                if (!(e.getDivergenceFromTarget().equals(divergenceFromTarget))) {
                                    e.setDivergenceFromTarget(divergenceFromTarget);
                                    viewModel.addEntry(e);
                                }
                            }
                        }
                        if (e.getBloodSugar() >= targetMin && e.getBloodSugar() <= targetMax) {
                            if (e.getDivergenceFromTarget() == null) {
                                e.setDivergenceFromTarget(0f);
                                viewModel.addEntry(e);
                            } else {
                                if (!(e.getDivergenceFromTarget().equals(0f))) {
                                    e.setDivergenceFromTarget(0f);
                                    viewModel.addEntry(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static Runnable getRunnableUpdateBolusCorrectionBSForAllEntries(DataViewModel viewModel, User user, List<Entry> entries, List<Notification> notifications, List<Daytime> daytimes) {
        return new UpdateBolusCorrectionBSForAllEntries(viewModel, user, entries, notifications, daytimes);
    }
    private static class UpdateBolusCorrectionBSForAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final User user;
        private final List<Entry> entries;
        private final List<Notification> notifications;
        private final List<Daytime> daytimes;

        UpdateBolusCorrectionBSForAllEntries(DataViewModel viewModel, User user, List<Entry> entries, List<Notification> notifications, List<Daytime> daytimes) {
            this.viewModel = viewModel;
            this.user = user;
            this.entries = entries;
            this.notifications = notifications;
            this.daytimes = daytimes;
        }

        @Override
        public void run() {
            boolean alreadyExisting = false;
            for (Entry e : entries) {
                if (e.getdIdF() == null) {
                    for (Notification n : notifications) {
                        if (n.getNotificationType() != null) {
                            if (n.getNotificationType().equals(Notification.DAYTIME_WARNING)) {
                                alreadyExisting = true;
                            }
                        }
                    }
                    if (!alreadyExisting) {
                        Notification n = new Notification();
                        n.setTimestamp(new Timestamp(System.currentTimeMillis()));
                        n.setNotificationType(Notification.DAYTIME_WARNING);
                        n.setMessage(Notification.MESSAGE_DAYTIME_WARNING);
                        viewModel.addNotification(n);
                        System.out.println("NEW NOTIFICATION ADDED (DAYTIME WARNING)");
                    }
                } else {
                    for (Daytime d : daytimes) {
                        if (e.getdIdF().equals(d.getdId())) {
                            Float correctionBS = 0f;
                            if (e.getDivergenceFromTarget() != null) {
                                if (e.getDivergenceFromTarget() > 0) correctionBS = (float)Math.ceil(e.getDivergenceFromTarget()/d.getCorrectionFactor());
                                if (e.getDivergenceFromTarget() < 0) correctionBS = (float)Math.floor(e.getDivergenceFromTarget()/d.getCorrectionFactor());
                            }
                            if (!(correctionBS.equals(0f))) {
                                if (e.getBolusCorrectionByBloodSugar() == null) {
                                    e.setBolusCorrectionByBloodSugar(correctionBS);
                                    viewModel.addEntry(e);
                                } else {
                                    if ((!e.getBolusCorrectionByBloodSugar().equals(correctionBS))) {
                                        e.setBolusCorrectionByBloodSugar(correctionBS);
                                        viewModel.addEntry(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Entry e : entries) {
                System.out.println("Bolus correction by blood sugar in entry " + e.geteId() + " was set to: " + e.getBolusCorrectionByBloodSugar());
            }
        }
    }

    public static Runnable getRunnableUpdateBolusCorrectionSportForAllEntries(DataViewModel viewModel, List<Entry> entries, List<Notification> notifications, List<Daytime> daytimes, List<Exercise> exercises, List<Sport> sports) {
        return new UpdateBolusCorrectionSportForAllEntries(viewModel, entries, notifications, daytimes, exercises, sports);
    }
    private static class UpdateBolusCorrectionSportForAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;
        private final List<Notification> notifications;
        private final List<Daytime> daytimes;
        private final List<Exercise> exercises;
        private final List<Sport> sports;

        UpdateBolusCorrectionSportForAllEntries(DataViewModel viewModel, List<Entry> entries, List<Notification> notifications, List<Daytime> daytimes, List<Exercise> exercises, List<Sport> sports) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.notifications = notifications;
            this.daytimes = daytimes;
            this.exercises = exercises;
            this.sports = sports;
        }

        @Override
        public void run() {
            boolean alreadyExisting = false;
            for (Entry e : entries) {
                if (e.getdIdF() == null) {
                    for (Notification n : notifications) {
                        if (n.getNotificationType() != null) {
                            if (n.getNotificationType().equals(Notification.DAYTIME_WARNING)) {
                                alreadyExisting = true;
                            }
                        }
                    }
                    if (!alreadyExisting) {
                        Notification n = new Notification();
                        n.setTimestamp(new Timestamp(System.currentTimeMillis()));
                        n.setNotificationType(Notification.DAYTIME_WARNING);
                        n.setMessage(Notification.MESSAGE_DAYTIME_WARNING);
                        viewModel.addNotification(n);
                        System.out.println("NEW NOTIFICATION ADDED (DAYTIME WARNING)");
                    }
                } else {
                    for (Daytime d : daytimes) {
                        if (e.getdIdF().equals(d.getdId())) {
                            List<Exercise> currentEntryExercises = new ArrayList<>();
                            for (Exercise ex : exercises) {
                                if (e.geteId().equals(ex.geteIdF())) {
                                    currentEntryExercises.add(ex);
                                }
                            }
                            if (currentEntryExercises.size() > 0) {
                                float effectSum = 0f;
                                for (Exercise exercise : currentEntryExercises) {
                                    for (Sport s : sports) {
                                        if (s.getsId().equals(exercise.getsIdF())) {
                                            effectSum += exercise.getExerciseUnits()*s.getSportEffectPerUnit();
                                        }
                                    }
                                }
                                float correctionSport = 0f;
                                if (effectSum > 0) {
                                    effectSum = effectSum*(-1f);
                                    correctionSport = (float)Math.floor(effectSum/d.getCorrectionFactor());
                                }
                                if (e.getBolusCorrectionBySport() == null) {
                                    e.setBolusCorrectionBySport(correctionSport);
                                    viewModel.addEntry(e);
                                } else {
                                    if (!(e.getBolusCorrectionBySport().equals(correctionSport))) {
                                        e.setBolusCorrectionBySport(correctionSport);
                                        viewModel.addEntry(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Entry e : entries) {
                System.out.println("BolusCorrectionSport in entry " + e.geteId() + " is: " + e.getBolusCorrectionBySport());
            }
        }
    }

    public static Runnable getRunnableRemoveNotificationDuplicates(DataViewModel viewModel, List<Notification> notifications) {
        return new RemoveNotificationDuplicates(viewModel, notifications);
    }
    private static class RemoveNotificationDuplicates implements Runnable {

        private final DataViewModel viewModel;
        private final List<Notification> notifications;

        RemoveNotificationDuplicates(DataViewModel viewModel, List<Notification> notifications) {
            this.viewModel = viewModel;
            this.notifications = notifications;
        }

        @Override
        public void run() {
            for (Notification n : notifications) {
                if (!(n.getNotificationType().equals(Notification.BASAL_INJECTION_FORGOTTEN)) && !(n.getNotificationType().equals(Notification.BASAL_RATIO_ADJUST))) {
                    for (Notification nn : notifications) {
                        if (n.getNotificationType().equals(nn.getNotificationType()) && !(n.getnId().equals(nn.getnId()))) {
                            viewModel.deleteNotification(nn);
                        }
                    }
                }
            }
        }
    }

    public static Runnable getRunnableUpdateBuFactorRealInAllEntries(DataViewModel viewModel, List<Entry> entries) {
        return new UpdateBuFactorReakInAllEntries(viewModel, entries);
    }
    private static class UpdateBuFactorReakInAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;

        UpdateBuFactorReakInAllEntries(DataViewModel viewModel, List<Entry> entries) {
            this.viewModel = viewModel;
            this.entries = entries;
        }

        @Override
        public void run() {
            for (Entry e : entries) {
                Float buFactorReal = 0f;
                if (e.getBreadUnit() != null && e.getBolus() != null) {
                    buFactorReal = e.getBolus()/e.getBreadUnit();
                    if (e.getBolusCorrectionByBloodSugar() != null) {
                        buFactorReal = (e.getBolus() + e.getBolusCorrectionByBloodSugar()) / e.getBreadUnit();
                    }
                    if (e.getBolusCorrectionBySport() != null) {
                        buFactorReal = (e.getBolus() + e.getBolusCorrectionBySport()) / e.getBreadUnit();
                    }
                    if (e.getBolusCorrectionByBloodSugar() != null && e.getBolusCorrectionBySport() != null) {
                        buFactorReal = (e.getBolus() + e.getBolusCorrectionByBloodSugar() + e.getBolusCorrectionBySport()) / e.getBreadUnit();
                    }
                }
                if (!(buFactorReal.equals(0f))) {
                    if (e.getBuFactorReal() == null) {
                        e.setBuFactorReal(buFactorReal);
                        viewModel.addEntry(e);
                    } else {
                        if (!(e.getBuFactorReal().equals(buFactorReal))) {
                            e.setBuFactorReal(buFactorReal);
                            viewModel.addEntry(e);
                        }
                    }
                }
            }
            for (Entry e : entries) {
                System.out.println("Bread unit factor real in entry " + e.geteId() + " was set to: " + e.getBuFactorReal());
            }
        }
    }

    public static Runnable getRunnableUpdateObservations(DataViewModel viewModel, User user, List<Observation> observations, List<Entry> entries, List<Notification> notifications) {
        return new UpdateObservations(viewModel, user, observations, entries, notifications);
    }
    private static class UpdateObservations implements Runnable {

        private final DataViewModel viewModel;
        private final User user;
        private final List<Observation> observations;
        private final List<Entry> entries;
        private final List<Notification> notifications;

        UpdateObservations(DataViewModel viewModel, User user, List<Observation> observations, List<Entry> entries, List<Notification> notifications) {
            this.viewModel = viewModel;
            this.user = user;
            this.observations = observations;
            this.entries = entries;
            this.notifications = notifications;
        }

        @Override
        public void run() {
            //create new observation if necessary





            //add new observation and to the database

            //update existing observations
            for (Observation o : observations) {

            }
        }
    }
}




