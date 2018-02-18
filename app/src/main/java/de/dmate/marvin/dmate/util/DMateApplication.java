package de.dmate.marvin.dmate.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.dmate.marvin.dmate.entities.Entry;

/**
 * Created by Marvin on 14.02.2018.
 */

public class DMateApplication extends Application {

    private SharedPreferences miscPrefs;
    private SharedPreferences entryPrefs;
    private Context context;
    private ArrayList<Entry> entries;

    public void initialize(Context context) {
        //set app context
        this.context = context;

        //initialize entryPrefs and miscPrefs attributes for easier access
        this.entryPrefs = context.getSharedPreferences("de.dmate.marvin.dmate.util.entryPrefs", Context.MODE_PRIVATE);
        this.miscPrefs = context.getSharedPreferences("de.dmate.marvin.dmate.util.miscPrefs", Context.MODE_PRIVATE);

        this.entries = getAllEntriesFromPrefs();
    }

    //save new entry in entryPrefs
    public void putEntry(Entry entry) {
        entries.add(entry);
        updateEntryPrefs();
    }

    //get all entries from DMateApplication.entries
    public ArrayList<Entry> getAllEntries(){
        return this.entries;
    }

    //get all entries from entryPrefs
    public ArrayList<Entry> getAllEntriesFromPrefs(){
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

    public void updateEntryPrefs() {
        SharedPreferences.Editor editor = entryPrefs.edit();
        //use GSON library to convert Entry-Object to JSON and save it to entryPrefs as string
        Gson gson = new Gson();

        for (Entry e : entries) {
            if (entryPrefs.getString(Long.toString(e.getDate().getTime()), null) == null){
                String json = gson.toJson(e);
                editor.putString(Long.toString(e.getDate().getTime()), json);
                editor.commit();
            }
        }
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
