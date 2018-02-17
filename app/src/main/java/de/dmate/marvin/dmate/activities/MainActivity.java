package de.dmate.marvin.dmate.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.util.ArrayList;

import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.util.DMateApplication;
import de.dmate.marvin.dmate.util.Helper;
import de.dmate.marvin.dmate.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set application context in DMateApplication (custom application object)
        DMateApplication app = (DMateApplication) getApplication();
        app.initialize(getApplicationContext());

        //pass Application Object to Helper
        Helper.getInstance().setApplication(app);

//        testing stuff
//        app.resetAllPrefs();
//        Entry a = Entry.bolus(7f).build();
//        Entry b = Entry.bloodsugar(100).breadunit(15.5f).bolus(31f).build();
//        Entry c = Entry.bloodsugar(127).note("TestNote").basal(18f).bolus(27f).breadunit(13.5f).build();
//        Entry d = Entry.bloodsugar(111).basal(7f).breadunit(7.5f).bolus(15f).note("Nooooooote").build();
        for (Entry e : app.getAllEntries()) {
            System.out.println(e.toString());
        }

        populateListView();
    }

    private void populateListView() {
        DMateApplication app = (DMateApplication)getApplication();
        ArrayList<Entry> entries = app.getAllEntries();

        ArrayAdapter<Entry> adapter = new ArrayAdapter<Entry>(this, R.layout.entry, entries);

        ListView listView = (ListView) findViewById(R.id.listview_main);
        listView.setAdapter(adapter);
    }
}
