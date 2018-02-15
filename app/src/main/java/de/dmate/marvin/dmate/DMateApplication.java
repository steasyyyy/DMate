package de.dmate.marvin.dmate;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Marvin on 14.02.2018.
 */

public class DMateApplication extends Application {

    private SharedPreferences prefs;
    private Context context;
    private int entryCount = 0;

    public void initialize(Context context) {
        //set app context
        this.context = context;

        //initialize prefs attribute for easier access
        this.prefs = context.getSharedPreferences("entries", Context.MODE_PRIVATE);

        //if entryCount has not been set yet, set it to 0
        if (getEntryCount()==-1) {
            setEntryCount(0);
        } else {
            //else get entryCount from prefs and initialize the entryCount attribute of this class
            this.entryCount = getEntryCount();
        }
    }

    //only internal use
    private int getEntryCount(){
        return this.prefs.getInt("entryCount", -1);
    }

    //only internal use
    private void setEntryCount(int entryCount){
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putInt("entryCount", entryCount);
        editor.commit();
    }

    //Use this method to get IDs while creating Entry Objects
    public int getNextID(){
        //save new entryCount as temp and write it to prefs before returning it
        int temp = this.prefs.getInt("entryCount", -1) + 1;
        setEntryCount(temp);
        return temp;
    }

    public void putEntry(Entry v) {
        SharedPreferences.Editor editor = this.prefs.edit();
        if(v.bloodsugar!=null) editor.putInt(v.id + "bloodsugar", v.bloodsugar);
        if(v.breadunit!=null) editor.putFloat(v.id + "breadunit", v.breadunit);
        if(v.bolus!=null) editor.putFloat(v.id + "bolus", v.bolus);
        if(v.basal!=null) editor.putFloat(v.id + "basal", v.basal);
        if(v.note!=null) editor.putString(v.id + "note", v.note);
        editor.commit();
    }

    public ArrayList<Entry> getEntries(){
        ArrayList<Entry> list = new ArrayList<Entry>();

        for (int i = 0; i+1< entryCount; i++) {
            String is = Integer.toString(i);
            if (this.prefs.getInt(is + "bloodsugar", -1)!=-1 ||
                    this.prefs.getFloat(is + "breadunit", -1f)!=-1f ||
                    this.prefs.getFloat(is + "bolus", -1f)!=-1f ||
                    this.prefs.getFloat(is + "basal", -1f) !=-1f ||
                    this.prefs.getString(is + "note", null) !=null)  {

                Entry.EntryBuilder temp = Entry.id(i);
                if (this.prefs.getInt(is + "bloodsugar", -1)!=-1) temp.bloodsugar(this.prefs.getInt(is + "bloodsugar", -1));
                if(this.prefs.getFloat(is + "breadunit", -1f)!=-1f) temp.breadunit(this.prefs.getFloat(is + "breadunit", -1f));
                if(this.prefs.getFloat(is + "bolus", -1f)!=-1f) temp.bolus(this.prefs.getFloat(is + "bolus", -1f));
                if(this.prefs.getFloat(is + "basal", -1f)!=-1f) temp.basal(this.prefs.getFloat(is + "basal", -1f));
                if(this.prefs.getString(is + "note", null)!=null) temp.note(this.prefs.getString(is + "note", null));

                list.add(temp.build());
            }
        }
        return list;
    }

    //for Testing purpose only
    //only internal use
    public  void resetPrefs() {
        prefs.edit().clear().commit();
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
