package de.dmate.marvin.dmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.util.DMateApplication;
import de.dmate.marvin.dmate.util.Helper;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_ENTRY_REQUEST = 1;
    public static final int EDIT_ENTRY_REQUEST = 2;

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

        //set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("DMate");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));

        //set up OnClickListener for FAB
        //when clicked, open NewAndUpdateEntryActivity and set requestCode to NEW_ENTRY_REQUEST
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewAndUpdateEntryActivity.class);
                intent.putExtra("REQUEST_CODE", NEW_ENTRY_REQUEST);
                startActivity(intent);
            }
        });

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

//        Entry ee = Entry.bloodsugar(55).basal(18f).breadunit(13.5f).bolus(15f).note("TestNote333333").build();
//        Entry f = Entry.bloodsugar(87).basal(2f).breadunit(17f).bolus(25f).note("Nooooooote1532458415").build();
//        Entry g = Entry.bloodsugar(139).basal(27f).breadunit(25.5f).bolus(30f).note("Test15").build();
//        Entry h = Entry.bolus(3f).build();
//        Entry i = Entry.bolus(5f).build();
//        Entry j = Entry.bolus(13f).build();
//        Entry k = Entry.bolus(20f).build();
//        Entry l = Entry.bolus(30f).build();
//        Entry m = Entry.bolus(27f).build();
//        Entry o = Entry.bolus(15f).build();
//        Entry p = Entry.bolus(1f).build();
//        Entry q = Entry.bolus(3f).build();
//        Entry r = Entry.bolus(9f).build();
//        Entry s = Entry.bolus(11f).build();
//        Entry t = Entry.bolus(7f).build();
//        Entry u = Entry.bolus(18f).build();
//        Entry v = Entry.bolus(32f).build();

//        Entry temp = app.getAllEntries().get(1);
//        System.out.println(temp.getDate().getTime() + "will be deleted");
//        app.deleteEntry(temp);

        for (Entry e : app.getAllEntries()) {
            System.out.println(e.toString());
        }

        //populate listView with entries and react to item clicks
        populateListView();
        registerClickCallback();
    }

    private void populateListView() {
        //instanciate an adapter and connect it to the listview
        ArrayAdapter<Entry> adapter = new CustomArrayAdapter();
        ListView listView = (ListView) findViewById(R.id.listview_main);
        listView.setAdapter(adapter);
    }

    //set up an ItemClickListener
    //react to the user clicking a certain item
    private void registerClickCallback() {
        ListView listView = (ListView) findViewById(R.id.listview_main);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NewAndUpdateEntryActivity.class);
                intent.putExtra("REQUEST_CODE", EDIT_ENTRY_REQUEST);
                intent.putExtra("POSITION", position);
                startActivity(intent);
            }
        });
    }

    //when resuming the activity, the dataset might have changed, so we need to update the listview!
    @Override
    protected void onResume() {
        ListView listView = (ListView) findViewById(R.id.listview_main);
        populateListView();
        super.onResume();
    }

    @Override
    //set menu (containing the actions) for the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_actions, menu);
        menu.findItem(R.id.action_save).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh :
                populateListView();
        }
        return super.onOptionsItemSelected(item);
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
            ViewHolder viewHolder;

            //get all entries from prefs
            ArrayList<Entry> entries = Helper.getInstance().getApplication().getAllEntries();

            //find entry to work with
            Entry currentEntry = entries.get(position);

            //make sure there is a view
            //create a view, if there is none
            if (convertView == null) {
                //inflate the layout
                convertView = getLayoutInflater().inflate(R.layout.entry_layout, parent, false);

                if (currentEntry.isLastEntryOfThisDay()) {
                    //get layout and set it visible if the entry is the last of the day
                    ConstraintLayout constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.constraintLayout_date_separator);
                    constraintLayout.setVisibility(View.VISIBLE);

                    //set lable for the date separator
                    String temp = Helper.formatMillisToDateString(currentEntry.getDateMillis());
                    TextView dateSeparatorTextView = (TextView) convertView.findViewById(R.id.textView_date_separator);
                    dateSeparatorTextView.setText(temp);

                    //set view unclickable
                    dateSeparatorTextView.setOnClickListener(null);
                    dateSeparatorTextView.setFocusable(false);
                }

                //set up viewholder
                viewHolder = new ViewHolder();

                viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.entry_date);
                viewHolder.bloodsugarTextView = (TextView) convertView.findViewById(R.id.entry_bloodsugar);
                viewHolder.breadunitTextView = (TextView) convertView.findViewById(R.id.entry_breadunit);
                viewHolder.bolusTextView = (TextView) convertView.findViewById(R.id.entry_bolus);
                viewHolder.basalTextView = (TextView) convertView.findViewById(R.id.entry_basal);

                viewHolder.staticdateTextView = (TextView) convertView.findViewById(R.id.static_date);
                viewHolder.staticbloodsugarTextView = (TextView) convertView.findViewById(R.id.static_bloodsugar);
                viewHolder.staticbreadunitTextView = (TextView) convertView.findViewById(R.id.static_breadunit);
                viewHolder.staticbolusTextView = (TextView) convertView.findViewById(R.id.static_bolus);
                viewHolder.staticbasalTextView = (TextView) convertView.findViewById(R.id.static_basal);

                //store the holder with the view
                convertView.setTag(viewHolder);
            } else {
                //if this is executed, one call of findViewById was avoided
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (currentEntry != null) {
                //fill the view
                String time = Helper.formatMillisToTimeString(currentEntry.getDateMillis());
                TextView dateTextView = (TextView) convertView.findViewById(R.id.entry_date);
                viewHolder.dateTextView.setText(time);

                if (currentEntry.getBloodsugar()!= null) {
                    viewHolder.bloodsugarTextView.setText(currentEntry.getBloodsugar().toString());
                }

                if (currentEntry.getBreadunit() != null) {
                    viewHolder.breadunitTextView.setText(currentEntry.getBreadunit().toString());
                }

                if (currentEntry.getBolus() != null) {
                    viewHolder.bolusTextView.setText(currentEntry.getBolus().toString());
                }

                if (currentEntry.getBasal() != null) {
                    viewHolder.basalTextView.setText(currentEntry.getBasal().toString());
                }
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView dateTextView;
        TextView bloodsugarTextView;
        TextView breadunitTextView;
        TextView bolusTextView;
        TextView basalTextView;

        TextView staticdateTextView;
        TextView staticbloodsugarTextView;
        TextView staticbreadunitTextView;
        TextView staticbolusTextView;
        TextView staticbasalTextView;
    }
}
