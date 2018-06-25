package de.dmate.marvin.dmate.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import de.dmate.marvin.dmate.services.BackgroundService;

public class DMateApplication extends Application {

    private BackgroundService service;
    private ServiceConnection serviceConnection;


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



    //check for Internet connection (not needed atm)
    /*
    public boolean isConnectingToInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }*/

}