package de.dmate.marvin.dmate.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.entities.EntryComparator;

public class DMateApplication extends Application {

    private SharedPreferences miscPrefs;
    private SharedPreferences entryPrefs;
    private ArrayList<Entry> entries;
    private Context context;

    public void initialize(Context context) {
        //initialize entryPrefs and miscPrefs attributes
        this.context = context;
        this.entryPrefs = context.getSharedPreferences("de.dmate.marvin.dmate.util.entryPrefs", Context.MODE_PRIVATE);
        this.miscPrefs = context.getSharedPreferences("de.dmate.marvin.dmate.util.miscPrefs", Context.MODE_PRIVATE);

        //get entries from prefs and save them as local list to prevent huge amount of reads from prefs
        this.entries = new ArrayList<Entry>();
        getAllEntriesFromPrefs();
    }

    //save new entry in entryPrefs
    public boolean putEntry(Entry entry) {
        //check if entry is already in the list
        for (Entry e: entries) {
            if (e.getDateMillis() == entry.getDateMillis()) return false;
        }

        this.entries.add(entry);

        resortEntries();
        updateEntryPrefs();
        return true;
    }

    public boolean deleteEntry(Entry entry) {
        for (Entry e : entries) {
            if (e.getDateMillis() == entry.getDateMillis()) {
                entries.remove(e);
                resortEntries();
                updateEntryPrefs();
                return true;
            }
        }
        return false;
    }

    //get all entries from DMateApplication.entries
    public ArrayList<Entry> getAllEntries(){
        return entries;
    }

    public Entry getEntry(int position) {
        return getAllEntries().get(position);
    }


    //get all entries from entryPrefs
    public void getAllEntriesFromPrefs(){
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
        Collections.sort(this.entries, new EntryComparator());
    }

    public void resortEntries() {
        Collections.sort(entries, new EntryComparator());
    }

    public void updateEntryPrefs() {
        SharedPreferences.Editor editor = entryPrefs.edit();
        //use GSON library to convert Entry-Object to JSON and save it to entryPrefs as string

        for (Entry e : entries) {
            //if entry with certain datetime does not exist yet, add it to prefs
            if (entryPrefs.getString(Long.toString(e.getDateMillis()), null) == null){
                Gson gson = new Gson();
                String json = gson.toJson(e);
                editor.putString(Long.toString(e.getDateMillis()), json);
                editor.commit();
            }
        }
    }

    //for Testing purpose only
    //only internal use
    public  void resetAllPrefs() {
        entryPrefs.edit().clear().commit();
        miscPrefs.edit().clear().commit();
        initialize(this.context);
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
