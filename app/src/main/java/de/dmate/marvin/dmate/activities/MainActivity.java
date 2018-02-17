package de.dmate.marvin.dmate.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.util.DMateApplication;
import de.dmate.marvin.dmate.util.Helper;
import de.dmate.marvin.dmate.R;

public class MainActivity extends AppCompatActivity {

    //auto generated stuff for the bottom navigation bar
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

        //populate listView with entries and react to item clicks
        populateListView();
        registerClickCallback();
    }

    private void populateListView() {
        //get list with all entries
        DMateApplication app = (DMateApplication)getApplication();
        ArrayList<Entry> entries = app.getAllEntries();

        //instanciate an adapter and connect it to the listview
        ArrayAdapter<Entry> adapter = new CustomArrayAdapter();
        ListView listView = (ListView) findViewById(R.id.listview_main);
        listView.setAdapter(adapter);
    }

    //sets up an ItemClickListener
    //react to the user clicking a certain item
    private void registerClickCallback() {
        ListView listView = (ListView) findViewById(R.id.listview_main);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                //TODO: What happens when a certain item is clicked
                //for now this is a dummy that displays a toast
                Entry clickedEntry = Helper.getInstance().app.getAllEntries().get(position);
                String message = "You clicked item #" + position + ".";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //CustomArrayAdapter allows to fill TextViews in entry_layout with data from Entry-Objects
    private class CustomArrayAdapter extends ArrayAdapter<Entry> {

        public CustomArrayAdapter() {
            super(MainActivity.this, R.layout.entry_layout, Helper.getInstance().getApplication().getAllEntries());
        }

        @NonNull
        @Override
        //returns a view for every entry there is in the list
        //position refers to positions in list of entries (app.getAllEntries())
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //make sure there is a view
            //create a view, if there is none
            View entryView = convertView;
            if (entryView == null) {
                entryView = getLayoutInflater().inflate(R.layout.entry_layout, parent, false);
            }

            //find entry to work with
            Entry currentEntry = Helper.getInstance().getApplication().getAllEntries().get(position);

            //fill the view
            Date date = currentEntry.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(date);
            TextView dateTextView = (TextView) entryView.findViewById(R.id.entry_date);
            dateTextView.setText(time);

            if (currentEntry.getBloodsugar()!= null) {
                TextView bloodsugarTextView = (TextView) entryView.findViewById(R.id.entry_bloodsugar);
                bloodsugarTextView.setText(currentEntry.getBloodsugar().toString());
            }

            if (currentEntry.getBreadunit() != null) {
                TextView breadunitTextView = (TextView) entryView.findViewById(R.id.entry_breadunit);
                breadunitTextView.setText(currentEntry.getBreadunit().toString());
            }

            if (currentEntry.getBolus() != null) {
                TextView bolusTextView = (TextView) entryView.findViewById(R.id.entry_bolus);
                bolusTextView.setText(currentEntry.getBolus().toString());
            }

            if (currentEntry.getBasal() != null) {
                TextView basalTextView = (TextView) entryView.findViewById(R.id.entry_basal);
                basalTextView.setText(currentEntry.getBasal().toString());
            }
            return entryView;
        }
    }
}
