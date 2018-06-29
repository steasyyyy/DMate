package de.dmate.marvin.dmate.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.dmate.marvin.dmate.activities.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationScheduler.showNotification(context, MainActivity.class);

    }
}