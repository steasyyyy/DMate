package de.dmate.marvin.dmate.util;

import android.content.Intent;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
                if (e.getdIdF() != null) {
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
                if (e.getdIdF() != null) {
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
                if (!(n.getNotificationType().equals(Notification.BASAL_INJECTION_FORGOTTEN))) {
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
        return new UpdateBuFactorRealInAllEntries(viewModel, entries);
    }
    private static class UpdateBuFactorRealInAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;

        UpdateBuFactorRealInAllEntries(DataViewModel viewModel, List<Entry> entries) {
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

    public static Runnable getRunnableUpdateBuFactorConsultingAfterResult(DataViewModel viewModel, User user, List<Entry> entries, List<Notification> notifications) {
        return new UpdateBuFactorConsultingAfterResult(viewModel, user, entries, notifications);
    }
    private static class UpdateBuFactorConsultingAfterResult implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;
        private final User user;
        private final List<Notification> notifications;

        UpdateBuFactorConsultingAfterResult(DataViewModel viewModel, User user, List<Entry> entries, List<Notification> notifications) {
            this.viewModel = viewModel;
            this.user = user;
            this.entries = entries;
            this.notifications = notifications;
        }

        @Override
        public void run() {
            Boolean alreadyExisting = false;
            if (user.getBolusDuration() == null) {
                for (Notification n : notifications) {
                    if (n.getNotificationType().equals(Notification.BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING)) {
                        alreadyExisting = true;
                    }
                }
                if (!alreadyExisting) {
                    Notification n = new Notification();
                    n.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    n.setNotificationType(Notification.BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING);
                    n.setMessage(Notification.MESSAGE_BOLUS_DURATION_OF_ACTION_NOT_SET_WARNING);
                    viewModel.addNotification(n);
                    System.out.println("NEW NOTIFICATION ADDED (BOLUS DURATION OF ACTION NOT SET)");
                }
            }
            if (user.getBolusDuration() != null) {
                float accMin = user.getBolusDuration() * 0.85f;
                float accMax = user.getBolusDuration() + 2f;
                long accMinMillis = (long)(accMin * 60 * 60 * 1000);
                long accMaxMillis = (long)(accMax * 60 * 60 * 1000);

                for (Entry e : entries) { //check if there are BE and Bolus first?
                    if (e.getBolus() != null && e.getBreadUnit() != null) {
                        Timestamp min = new Timestamp(e.getTimestamp().getTime() + accMinMillis);
                        Timestamp max = new Timestamp(e.getTimestamp().getTime() + accMaxMillis);

                        for (Entry e2 : entries) {
                            if (e2.getTimestamp().after(min) && e2.getTimestamp().before(max)) {
                                if (e.geteIdResultF() == null) {
                                    e.seteIdResultF(e2.geteId());
                                    viewModel.addEntry(e);
                                } else {
                                    if (!(e.geteIdResultF().equals(e2.geteId()))) {
                                        e.seteIdResultF(e2.geteId());
                                        viewModel.addEntry(e);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            for (Entry e : entries) {
                if (e.geteIdResultF() != null) {
                    Entry result = null;
                    for (Entry e2 : entries) {
                        if (e2.geteId().equals(e.geteIdResultF())) {
                            result = e2;
                        }
                    }
                    if (result == null) {
                        e.seteIdResultF(null);
                        viewModel.addEntry(e);
                    } else {
                        Float buFactorConsultingAfterResult = 0f;
                        if (result.getBolusCorrectionByBloodSugar() != null) {
                            if (!(result.getBolusCorrectionByBloodSugar().equals(0f))) {
                                buFactorConsultingAfterResult = (e.getBreadUnit() * e.getBuFactorReal() + result.getBolusCorrectionByBloodSugar()) / e.getBreadUnit();
                                if (e.getBuFactorCunsultingAfterResult() == null) {
                                    e.setBuFactorCunsultingAfterResult(buFactorConsultingAfterResult);
                                    viewModel.addEntry(e);
                                } else {
                                    if (!(e.getBuFactorCunsultingAfterResult().equals(buFactorConsultingAfterResult))) {
                                        e.setBuFactorCunsultingAfterResult(buFactorConsultingAfterResult);
                                        viewModel.addEntry(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Entry e : entries) {
                System.out.println("Bread unit factor consulting after result in entry " + e.geteId() + " was set to: " + e.getBuFactorCunsultingAfterResult());
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
                Float amount = 0f;
                Integer divideBy = 0;
                Float result = 0f;
                for (Entry e : entries) {
                    if (e.getdIdF() != null && e.getReliable()) {
                        if (e.getdIdF().equals(d.getdId())) {
                            if (e.getBuFactorCunsultingAfterResult() != null) {
                                amount += e.getBuFactorCunsultingAfterResult();
                                divideBy++;
                            }
                        }
                    }
                }
                if (!amount.equals(0f) && !divideBy.equals(0)) {
                    result = amount/divideBy;
                }

                if (!result.equals(0f)) {
                    if (d.getBuFactorConsultingArithMean() == null) {
                        d.setBuFactorConsultingArithMean(result);
                        viewModel.addDaytime(d);
                    } else {
                        if (!(d.getBuFactorConsultingArithMean().equals(result))) {
                            d.setBuFactorConsultingArithMean(result);
                            viewModel.addDaytime(d);
                        }
                    }
                }
            }
            for (Daytime d : daytimes) {
                System.out.println("Bread unit factor (consulting) arith mean of daytime " + d.getdId() + " was set to: " + d.getBuFactorConsultingArithMean());
            }
        }
    }

    public static Runnable getRunnableUpdateBuFactorConsulting(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
        return new UpdateBuFactorConsulting(viewModel, entries, daytimes);
    }
    private static class UpdateBuFactorConsulting implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;
        private final List<Daytime> daytimes;

        UpdateBuFactorConsulting(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.daytimes = daytimes;
        }

        @Override
        public void run() {
            for (Entry e : entries) {
                if (e.getdIdF() != null) {
                    for (Daytime d : daytimes) {
                        if (d.getBuFactorConsultingArithMean() != null) {
                            if (e.getdIdF().equals(d.getdId())) {
                                if (e.getBuFactorConsulting() == null) {
                                    e.setBuFactorConsulting(d.getBuFactorConsultingArithMean());
                                    viewModel.addEntry(e);
                                } else {
                                    if (!(e.getBuFactorConsulting().equals(d.getBuFactorConsultingArithMean()))) {
                                        e.setBuFactorConsulting(d.getBuFactorConsultingArithMean());
                                        viewModel.addEntry(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Entry e : entries) {
                System.out.println("Bread unit factor consulting in entry " + e.geteId() + " was set to: " + e.getBuFactorConsulting());
            }
        }
    }

    public static Runnable getRunnableUpdateReqBolusConsultingInAllEntries(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
        return new UpdateReqBolusConsultingInAllEntries(viewModel, entries, daytimes);
    }
    private static class UpdateReqBolusConsultingInAllEntries implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;
        private final List<Daytime> daytimes;

        UpdateReqBolusConsultingInAllEntries(DataViewModel viewModel, List<Entry> entries, List<Daytime> daytimes) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.daytimes = daytimes;
        }

        @Override
        public void run() {
            for (Entry e : entries) {
                Float amount = 0f;
                if (e.getBolusCorrectionByBloodSugar() != null) {
                    amount = e.getBolusCorrectionByBloodSugar();
                }
                if (e.getBolusCorrectionBySport() != null) {
                    amount = e.getBolusCorrectionBySport();
                }
                if (e.getBolusCorrectionByBloodSugar() != null && e.getBolusCorrectionBySport() != null) {
                    amount = e.getBolusCorrectionByBloodSugar() + e.getBolusCorrectionBySport();
                }
                if (e.getBreadUnit() != null) {
                    if (e.getBuFactorConsulting() != null) {
                        amount += e.getBreadUnit() * e.getBuFactorConsulting();
                        if (e.getReqBolusConsulting() == null) {
                            e.setReqBolusConsulting(amount);
                            viewModel.addEntry(e);
                        } else {
                            if (!(e.getReqBolusConsulting().equals(amount))) {
                                e.setReqBolusConsulting(amount);
                                viewModel.addEntry(e);
                            }
                        }
                    }
                }
            }
            for (Entry e : entries) {
                System.out.println("Required bolus (consulting) in entry " + e.geteId() + " was set to: " + e.getReqBolusConsulting());
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
            long threeHoursInMillis = 3*60*60*1000; //10800000ms
            long fiveHoursInMillis = 5*60*60*1000; //18000000ms
            Observation oNew = null;

            for (Entry start : entries) {
                Timestamp min = new Timestamp(start.getTimestamp().getTime() + threeHoursInMillis);
                Timestamp max = new Timestamp(start.getTimestamp().getTime() + fiveHoursInMillis);
                Entry end = null;
                if (start.getBloodSugar() != null && start.getBolus() == null) {
                    Boolean interruptedByBolus = false;
                    for (Entry e2 : entries) {
                        if (e2.getTimestamp().after(start.getTimestamp()) && e2.getTimestamp().before(min)) {
                            if (e2.getBolus() != null) {
                                interruptedByBolus = true;
                                break;
                            }
                        }
                    }
                    if (!interruptedByBolus) {
                        for (Entry e3 : entries) {
                            if (e3.getTimestamp().after(min) && e3.getTimestamp().before(max)) {
                                if (e3.getBloodSugar() != null) {
                                    end = e3;
                                    break;
                                }
                            }
                        }
                        if (end == null) {
                            oNew = new Observation();
                            oNew.setEIdStart(start.geteId());
                        }
                    }
                    if (end != null) {
                        Float divergence = end.getBloodSugar() - start.getBloodSugar();
                        oNew = new Observation();
                        oNew.setEIdStart(start.geteId());
                        oNew.setEIdEnd(end.geteId());
                        oNew.setDivergenceFromStart(divergence);
                    }
                }
            }
            Boolean alreadyExisting = false;
            if (oNew != null) {
                for (Observation o : observations) {
                    if (oNew.getEIdStart()!= null) {
                        if (oNew.getEIdStart().equals(o.getEIdStart())) {
                            alreadyExisting = true;
                        }
                    }
                    if (oNew.getEIdEnd() != null) {
                        if (oNew.getEIdEnd().equals(o.getEIdEnd())) {
                            alreadyExisting = true;
                        }
                    }
                }
                if (!alreadyExisting && oNew != null) {
                    viewModel.addObservation(oNew);
                }
            }
            for (Observation o : observations) {
                Entry start = null;
                Entry end = null;
                for (Entry e : entries) {
                    if (e.geteId().equals(o.getEIdStart())) {
                        start = e;
                        break;
                    }
                }
                if (start == null) {
                    viewModel.deleteObservation(o);
                } else {
                    if (start.getBolus() != null) {
                        viewModel.deleteObservation(o);
                    }
                    Timestamp min = new Timestamp(start.getTimestamp().getTime() + threeHoursInMillis);
                    Timestamp max = new Timestamp(start.getTimestamp().getTime() + fiveHoursInMillis);
                    if (start.getBloodSugar() != null && start.getBolus() == null) {
                        Boolean interruptedByBolus = false;
                        for (Entry e2 : entries) {
                            if (e2.getTimestamp().after(start.getTimestamp()) && e2.getTimestamp().before(min)) {
                                if (e2.getBolus() != null) {
                                    interruptedByBolus = true;
                                }
                            }
                        }
                        if (interruptedByBolus) {
                            viewModel.deleteObservation(o);
                        } else {
                            for (Entry e3 : entries) {
                                if (e3.getTimestamp().after(min) && e3.getTimestamp().before(max)) {
                                    if (e3.getBloodSugar() != null) {
                                        end = e3;
                                        break;
                                    }
                                }
                            }
                            if (end != null) {
                                if (o.getEIdEnd() == null) {
                                    o.setEIdEnd(end.geteId());
                                    if (o.getDivergenceFromStart() == null) {
                                        o.setDivergenceFromStart(end.getBloodSugar() - start.getBloodSugar());
                                        viewModel.addObservation(o);
                                    } else {
                                        if (!(o.getDivergenceFromStart().equals(end.getBloodSugar() - start.getBloodSugar()))) {
                                            o.setDivergenceFromStart(end.getBloodSugar() - start.getBloodSugar());
                                            viewModel.addObservation(o);
                                        }
                                    }
                                } else {
                                    if (!(o.getEIdEnd().equals(end.geteId()))) {
                                        o.setEIdEnd(end.geteId());
                                        viewModel.addObservation(o);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Observation o : observations) {
                System.out.println("Observation " + o.getOId() + " has StartEntryID " + o.getEIdStart() + " and EndEntryID " + o.getEIdEnd() + ", divergence from start is " + o.getDivergenceFromStart());
            }
        }
    }

    public static Runnable getRunnableUpdateDivergenceFromStartValueArithMean(DataViewModel viewModel, User user, List<Entry> entries, List<Observation> observations) {
        return new UpdateDivergenceFromInitialValueArithMean(viewModel, user, entries, observations);
    }
    private static class UpdateDivergenceFromInitialValueArithMean implements Runnable {

        private final DataViewModel viewModel;
        private final User user;
        private final List<Entry> entries;
        private final List<Observation> observations;

        UpdateDivergenceFromInitialValueArithMean(DataViewModel viewModel, User user, List<Entry> entries, List<Observation> observations) {
            this.viewModel = viewModel;
            this.user = user;
            this.entries = entries;
            this.observations = observations;
        }

        @Override
        public void run() {
            Float divergenceSum = 0f;
            Integer divideBy = 0;
            Float result = 0f;
            for (Observation o : observations) {
                Boolean inPastTwoWeeks = false;
                for (Entry e : entries) {
                    if (o.getEIdStart() != null) {
                        if (o.getEIdStart().equals(e.geteId())) {
                            inPastTwoWeeks = true;
                        }
                    }
                    if (o.getEIdEnd() != null) {
                        if (o.getEIdEnd().equals(e.geteId())) {
                            inPastTwoWeeks = true;
                        }
                    }
                }
                if (inPastTwoWeeks) {
                    if (o.getDivergenceFromStart() != null) {
                        divergenceSum += o.getDivergenceFromStart();
                        divideBy++;
                    }
                }
            }
            if (divergenceSum != 0f && divideBy != 0) {
                result = divergenceSum / divideBy;
            }
            if (result != 0f) {
                if (user.getDivergenceFromInitialValueArithMean() == null) {
                    user.setDivergenceFromInitialValueArithMean(result);
                    viewModel.addUser(user);
                } else {
                    if (!(user.getDivergenceFromInitialValueArithMean().equals(result))) {
                        user.setDivergenceFromInitialValueArithMean(result);
                        viewModel.addUser(user);
                    }
                }
            }
            System.out.println("Divergence from initial value arith mean in User was set to: " + user.getDivergenceFromInitialValueArithMean());
        }
    }

    public static Runnable getRunnableRemoveObservationDuplicates(DataViewModel viewModel, List<Observation> observations) {
        return new RemoveObservationDuplicates(viewModel, observations);
    }
    private static class RemoveObservationDuplicates implements Runnable {

        private final DataViewModel viewModel;
        private final List<Observation> observations;

        RemoveObservationDuplicates(DataViewModel viewModel, List<Observation> observations) {
            this.viewModel = viewModel;
            this.observations = observations;
        }

        @Override
        public void run() {
            for (Observation o1 : observations) {
                if (o1.getEIdStart() == null) {
                    viewModel.deleteObservation(o1);
                } else {
                    for (Observation o2 : observations) {
                        if (o1.getOId() != o2.getOId()) {
                            if (o2.getEIdStart() != null) {
                                if (o1.getEIdStart().equals(o2.getEIdStart())) {
                                    viewModel.deleteObservation(o1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static Runnable getRunnableTriggerAdjustBasalNotification(DataViewModel viewModel, User user) {
        return new TriggerAdjustBasalNotification(viewModel, user);
    }
    private static class TriggerAdjustBasalNotification implements Runnable {

        private final DataViewModel viewModel;
        private final User user;

        TriggerAdjustBasalNotification(DataViewModel viewModel, User user) {
            this.viewModel = viewModel;
            this.user = user;
        }

        @Override
        public void run() {
            Float divergenceAllowedMin = 0f;
            Float divergenceAllowedMax = 0f;

            if (user.getBloodsugarArithMean() != null && user.getDivergenceFromInitialValueArithMean() != null) {
                divergenceAllowedMin = user.getBloodsugarArithMean() * (-0.15f);
                divergenceAllowedMax = user.getBloodsugarArithMean() * 0.15f;
                if (user.getDivergenceFromInitialValueArithMean() < divergenceAllowedMin || user.getDivergenceFromInitialValueArithMean() > divergenceAllowedMax) {
                    //trigger Notification if not existing already
                }
            }
        }
    }

    public static Runnable getRunnableUpdateExternalNotificationTimePBI(DataViewModel viewModel, List<Entry> entries, List<PlannedBasalInjection> plannedBasalInjections, List<Notification> notifications) {
        return new UpdateExternalNotificationTimePBI(viewModel, entries, plannedBasalInjections, notifications);
    }
    private static class UpdateExternalNotificationTimePBI implements Runnable {

        private final DataViewModel viewModel;
        private final List<Entry> entries;
        private final List<PlannedBasalInjection> plannedBasalInjections;
        private final List<Notification> notifications;

        UpdateExternalNotificationTimePBI(DataViewModel viewModel, List<Entry> entries, List<PlannedBasalInjection> plannedBasalInjections, List<Notification> notifications) {
            this.viewModel = viewModel;
            this.entries = entries;
            this.plannedBasalInjections = plannedBasalInjections;
            this.notifications = notifications;
        }

        @Override
        public void run() {
            if (plannedBasalInjections.size() == 0) {
                Boolean alreadyExisting = false;
                for (Notification n : notifications) {
                    if (n.getNotificationType().equals(Notification.PLANNED_BASAL_INJECTIONS_NOT_SET)) {
                        alreadyExisting = true;
                    }
                }
                if (!alreadyExisting) {
                    Notification newN = new Notification();
                    newN.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    newN.setNotificationType(Notification.PLANNED_BASAL_INJECTIONS_NOT_SET);
                    newN.setMessage(Notification.MESSAGE_PLANNED_BASAL_INJECTIONS_NOT_SET);
                    viewModel.addNotification(newN);
                }
            } else {
                for (PlannedBasalInjection pbi : plannedBasalInjections) {
                    Calendar c = Helper.getCalendarFromTimeString(pbi.getTimeOfDay());


                    //add notification to the database and fire a notification in the system at c.getTime
                }
            }
        }
    }

    public static Runnable getRunnableCREATETESTDATA(DataViewModel viewModel, User user) {
        return new CREATETESTDATA(viewModel, user);
    }
    private static class CREATETESTDATA implements Runnable {

        private final DataViewModel viewModel;
        private final User user;

        CREATETESTDATA(DataViewModel viewModel, User user) {
            this.viewModel = viewModel;
            this.user = user;
        }

        @Override
        public void run() {
            user.setName("Marvin");
            user.setBolusName("Novorapid");
            user.setBolusDuration(2f);
            user.setBasalName("Toujeo");
            user.setBasalDuration(48f);
            user.setTargetMin(80f);
            user.setTargetMax(140f);
            user.setUnitBu(true);
            user.setUnitMgdl(true);
            user.setNotificationsEnabled(true);
            viewModel.addUser(user);

            Daytime d1 = new Daytime();
            d1.setdId(1);
            d1.setDaytimeStart("05:00");
            d1.setDaytimeEnd("12:00");
            d1.setCorrectionFactor(30f);
            d1.setBuFactor(1.8f);
            viewModel.addDaytime(d1);

            Daytime d2 = new Daytime();
            d2.setdId(2);
            d2.setDaytimeStart("12:00");
            d2.setDaytimeEnd("17:00");
            d2.setCorrectionFactor(35f);
            d2.setBuFactor(1.9f);
            viewModel.addDaytime(d2);

            Daytime d3 = new Daytime();
            d3.setdId(3);
            d3.setDaytimeStart("17:00");
            d3.setDaytimeEnd("05:00");
            d3.setCorrectionFactor(40f);
            d3.setBuFactor(2.0f);
            viewModel.addDaytime(d3);

            PlannedBasalInjection p = new PlannedBasalInjection();
            p.setPbiId(1);
            p.setTimeOfDay("15:00");
            p.setBasal(14f);
            viewModel.addPlannedBasalInjection(p);

            Sport s1 = new Sport();
            s1.setsId(1);
            s1.setSportName("Tanzen");
            s1.setSportEffectPerUnit(30f);
            viewModel.addSport(s1);

            Sport s2 = new Sport();
            s2.setsId(2);
            s2.setSportName("Joggen");
            s2.setSportEffectPerUnit(50f);
            viewModel.addSport(s2);

            Sport s3 = new Sport();
            s3.setsId(3);
            s3.setSportName("Arbeiten");
            s3.setSportEffectPerUnit(20f);
            viewModel.addSport(s3);

            Calendar c = Calendar.getInstance();

            Entry e1 = new Entry();
            c.set(2018,5,22,13,22,21);
            e1.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e1.seteId(1);
            e1.setBloodSugar(97f);
            e1.setBreadUnit(7f);
            e1.setBolus(13f);
            viewModel.addEntry(e1);

            Entry e2 = new Entry();
            c.set(2018,5,22,15,35,7);
            e2.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e2.seteId(2);
            e2.setBloodSugar(157f);
            e2.setBolus(1f);
            viewModel.addEntry(e2);

            Entry e3 = new Entry();
            c.set(2018,5,22,18,11,50);
            e3.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e3.seteId(3);
            e3.setBloodSugar(123f);
            viewModel.addEntry(e3);

            Entry e4 = new Entry();
            c.set(2018,5,22,19,47,13);
            e4.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e4.seteId(4);
            e4.setBloodSugar(115f);
            e4.setBreadUnit(13f);
            e4.setBolus(26f);
            e4.setNote("spaghetti carbonara");
            viewModel.addEntry(e4);

            Entry e5 = new Entry();
            c.set(2018,5,22,23,27,40);
            e5.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e5.setBloodSugar(137f);
            e5.setBasal(14f);
            viewModel.addEntry(e5);

            Entry e6 = new Entry();
            c.set(2018,5,23,7,38,33);
            e6.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e6.seteId(6);
            e6.setBloodSugar(117f);
            e6.setBreadUnit(7.5f);
            e6.setBolus(13f);
            viewModel.addEntry(e6);

            Entry e7 = new Entry();
            c.set(2018,5,23,10,01,16);
            e7.setTimestamp(new Timestamp(c.getTimeInMillis()));
            e7.seteId(7);
            e7.setBloodSugar(196f);
        }
    }
}




