package de.dmate.marvin.dmate.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marvin on 14.02.2018.
 */

public class Helper {

    //HELPER CLASS to access Application Object from everywhere
    //EXAMPLE: Helper.getInstance().getApplication().getNextID();

    private static final Helper ourInstance = new Helper();
    public DMateApplication app;

    public static Helper getInstance() {
        return ourInstance;
    }

    private Helper() {
    }

    public void setApplication(DMateApplication application) {
        app = application;
    }

    public DMateApplication getApplication() {
        return app;
    }

    public static String formatMillisToDateString(Long dateMillis) {
        Date date = new Date(dateMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMMM dd yyyy");
        String temp = sdf.format(date);
        return temp;
    }

    public static String formatMillisToTimeString(Long dateMillis) {
        Date date = new Date(dateMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(date);
        return time;
    }
}
