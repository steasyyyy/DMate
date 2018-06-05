package de.dmate.marvin.dmate.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.dmate.marvin.dmate.roomDatabase.DataViewModel;

public class Helper {

    //HELPER CLASS to access Application Object from everywhere
    //EXAMPLE: Helper.getInstance().getApplication().getNextID();

    private static final Helper instance = new Helper();
    private DMateApplication app;
    private EntriesRecyclerViewAdapter entriesRecyclerViewAdapter;
    private DataViewModel dataViewModel;

    public static Helper getInstance() {
        return instance;
    }

    private Helper() {
    }

    public void setApplication(DMateApplication application) {
        app = application;
    }

    public DMateApplication getApplication() {
        return app;
    }

    public void setEntriesRecyclerViewAdapter(EntriesRecyclerViewAdapter entriesRecyclerViewAdapter) {
        this.entriesRecyclerViewAdapter = entriesRecyclerViewAdapter;
    }

    public EntriesRecyclerViewAdapter getEntriesRecyclerViewAdapter() {
        return this.entriesRecyclerViewAdapter;
    }

    public void setDataViewModel(DataViewModel dataViewModel) {
        this.dataViewModel = dataViewModel;
    }

    public DataViewModel getDataViewModel() {
        return this.dataViewModel;
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
