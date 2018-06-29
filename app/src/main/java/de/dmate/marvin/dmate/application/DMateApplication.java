package de.dmate.marvin.dmate.application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.Calendar;

import de.dmate.marvin.dmate.activities.MainActivity;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.services.BackgroundService;
import de.dmate.marvin.dmate.util.AlarmReceiver;

public class DMateApplication extends Application {

    private BackgroundService service;
    private ServiceConnection serviceConnection;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                service = ((BackgroundService.LocalBinder) iBinder ).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //possibility to restart service on disconnect (behaviour not tested yet)
//                Intent intent = new Intent(DMateApplication.this, BackgroundService.class);
//                getApplicationContext().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            }
        };

        Intent intent = new Intent(this, BackgroundService.class);
        getApplicationContext().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
}
