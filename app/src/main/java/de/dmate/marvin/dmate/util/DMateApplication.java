package de.dmate.marvin.dmate.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import de.dmate.marvin.dmate.entities.Entry;

/**
 * Created by Marvin on 14.02.2018.
 */

public class DMateApplication extends Application {

    private SharedPreferences miscPrefs;
    private SharedPreferences entryPrefs;
    private Context context;

    public void initialize(Context context) {
        //set app context
        this.context = context;

        //initialize entryPrefs and miscPrefs attributes for easier access
        this.entryPrefs = context.getSharedPreferences("entryPrefs", Context.MODE_PRIVATE);
        this.miscPrefs = context.getSharedPreferences("miscPrefs", Context.MODE_PRIVATE);

        //if entryCount does not exist, set it to 0
        if (getEntryCount() == -1) {
            setEntryCount(0);
        }
    }

    public int getEntryCount() {
        return miscPrefs.getInt("entryCount", -1);
    }

    public void setEntryCount(Integer entryCount) {
        SharedPreferences.Editor editor = miscPrefs.edit();
        editor.putInt("entryCount", entryCount);
        editor.commit();
    }

    //save new entry in entryPrefs
    public void putEntry(Entry entry) {
        if (entryPrefs.getString(Long.toString(entry.getDate().getTime()), null) == null) {
            SharedPreferences.Editor editor = entryPrefs.edit();
            //use GSON library to convert Entry-Object to JSON and save it to entryPrefs as string
            Gson gson = new Gson();
            String json = gson.toJson(entry);
            editor.putString(Long.toString(entry.getDate().getTime()), json);
            editor.commit();
            updateEntryCount();
        }
    }

    //update entryCount
    //CALL WHEN PUTTING NEW ENTRIES OR DELETING ENTRIES
    public void updateEntryCount() {
        setEntryCount(getAllEntries().size());
    }

    //get Entry by ID from entryPrefs (ID is defined by Date in millis)
    public Entry getEntry(long timeMillis) {
        if (entryPrefs.getString(Long.toString(timeMillis), null) == null) return null;

        Gson gson = new Gson();
        String json = entryPrefs.getString(Long.toString(timeMillis), null);
        return gson.fromJson(json, Entry.class);
    }

    //get all entries from entryPrefs
    public ArrayList<Entry> getAllEntries(){
        final ArrayList<Entry> entries = new ArrayList<Entry>();

            Map<String,?> keys = entryPrefs.getAll();

            ArrayList<String> strings = new ArrayList<String>();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                if (entry.getValue() instanceof String) {
                    strings.add((String) entry.getValue());
                }
            }

            Gson gson = new Gson();
            for (String json : strings) {
                Entry e = gson.fromJson(json, Entry.class);
                entries.add(e);
            }
        Collections.sort(entries, new EntryComparator());
        return entries;
    }

    //for Testing purpose only
    //only internal use
    public  void resetAllPrefs() {
        entryPrefs.edit().clear().commit();
        miscPrefs.edit().clear().commit();
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
