package de.dmate.marvin.dmate;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        SharedPreferences.Editor editor = entryPrefs.edit();
        //use GSON library to convert Entry-Object to JSON and save it to entryPrefs as string
        Gson gson = new Gson();
        String json = gson.toJson(entry);
        System.out.println(json);
        editor.putString(entry.date.toString(), json);
        editor.commit();
        updateEntryCount();
    }

    //update entryCount
    //CALL WHEN PUTTING NEW ENTRIES OR DELETING ENTRIES
    public void updateEntryCount() {
        setEntryCount(getAllEntries().size());
    }

    //get Entry by ID from entryPrefs
    public Entry getEntry(String date) {
        if (entryPrefs.getString(date, null) == null) return null;

        Gson gson = new Gson();
        String json = entryPrefs.getString(date, null);
        return gson.fromJson(json, Entry.class);
    }

    //get all entries from entryPrefs
    public ArrayList<Entry> getAllEntries(){
        final ArrayList<Entry> entries = new ArrayList<Entry>();

        //run in thread to avoid problems with large collection of entries
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //use GSON library to read JSONs from entryPrefs and convert them to Entry-Objects
                Gson gson = new Gson();

                //get all keys from prefs before iterating through them
                ArrayList<String> keyList = new ArrayList<String>();
                keyList.addAll(entryPrefs.getAll().keySet());

                //for each key in the keylist, extract json and convert it to an Entry-Object
                for (String s : keyList) {
                    System.out.println("Keylist value" + s);
                    String json = entryPrefs.getString(s, null);
                    Entry e = gson.fromJson(json, Entry.class);
                    entries.add(e);
                }
            }
        });
        System.out.println("SIZE:" + entries.size());
        for (Entry e : entries) {
            System.out.println(e.toString());
        }
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
