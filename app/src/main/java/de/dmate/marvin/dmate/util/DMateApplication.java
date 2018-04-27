package de.dmate.marvin.dmate.util;

import android.app.Application;
import android.content.Context;

public class DMateApplication extends Application {

    private Context context;

    public void initialize(Context context) {
        //initialize entryPrefs and miscPrefs attributes
        this.context = context;
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
