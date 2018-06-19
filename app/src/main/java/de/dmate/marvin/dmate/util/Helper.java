package de.dmate.marvin.dmate.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    private static final Helper instance = new Helper();
    private DMateApplication app;
    private EntriesRecyclerViewAdapter entriesRecyclerViewAdapter;

    public static Helper getInstance() {
        return instance;
    }

    private Helper() {

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

    //getters and setters
    public DMateApplication getApp() {
        return app;
    }

    public void setApp(DMateApplication app) {
        this.app = app;
    }

    public EntriesRecyclerViewAdapter getEntriesRecyclerViewAdapter() {
        return entriesRecyclerViewAdapter;
    }

    public void setEntriesRecyclerViewAdapter(EntriesRecyclerViewAdapter entriesRecyclerViewAdapter) {
        this.entriesRecyclerViewAdapter = entriesRecyclerViewAdapter;
    }
}
