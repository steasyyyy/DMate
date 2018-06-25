package de.dmate.marvin.dmate.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    private static final Helper instance = new Helper();
    private DMateApplication app;
    private RecyclerViewAdapterEntries recyclerViewAdapterEntries;

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

    //WARNING: ONLY FOR TIME FORMAT "HH:MM"
    public static Calendar getCalendarFromTimeString(String s) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.charAt(0) + "" + s.charAt(1)));
        c.set(Calendar.MINUTE, Integer.parseInt(s.charAt(3) + "" + s.charAt(4)));
        return c;
    }

    public static Timestamp getTimestampFromTimeString(String s) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.charAt(0) + "" + s.charAt(1)));
        c.set(Calendar.MINUTE, Integer.parseInt(s.charAt(3) + "" + s.charAt(4)));
        Timestamp t = new Timestamp(c.getTimeInMillis());
        return t;
    }

    public static Timestamp stringToTimestamp(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        java.util.Date parsedDate;
        try {
            parsedDate = dateFormat.parse(timestamp);
        } catch (ParseException e) {
            System.out.println("Wrong string format to parse to Timestamp");
            parsedDate = new Timestamp(System.currentTimeMillis());
        }
        return timestamp == null ? null : new Timestamp(parsedDate.getTime());
    }

    public static String timestampToString(Timestamp timestamp) {
        return timestamp.toString();
    }

    //getters and setters
    public DMateApplication getApp() {
        return app;
    }

    public void setApp(DMateApplication app) {
        this.app = app;
    }

    public RecyclerViewAdapterEntries getRecyclerViewAdapterEntries() {
        return recyclerViewAdapterEntries;
    }

    public void setRecyclerViewAdapterEntries(RecyclerViewAdapterEntries recyclerViewAdapterEntries) {
        this.recyclerViewAdapterEntries = recyclerViewAdapterEntries;
    }
}
