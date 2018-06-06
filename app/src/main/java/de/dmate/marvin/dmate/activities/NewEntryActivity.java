package de.dmate.marvin.dmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Timestamp;
import java.util.Calendar;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.PickerFragments.DatePickerFragment;
import de.dmate.marvin.dmate.fragments.PickerFragments.TimePickerFragment;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.util.EntriesRecyclerViewAdapter;
import de.dmate.marvin.dmate.util.Helper;

public class NewEntryActivity extends AppCompatActivity
        implements TimePickerFragment.OnTimePickerFragmentInteractionListener, DatePickerFragment.OnDatePickerFragmentInteractionListener {

    private int requestCode;

    private Button dateButton;
    private Button timeButton;
    private EditText ETbloodsugar;
    private EditText ETbreadunit;
    private EditText ETbolus;
    private EditText ETbasal;
    private EditText ETnote;
    private Button exercisesButton;
    private Switch diseasedSwitch;

    private SlidingUpPanelLayout supl;
    private TextView slideUpTextView;
    private ImageView slideUpImageView;

    private Calendar calendar;

    private RecyclerView recyclerView;
    private EntriesRecyclerViewAdapter entriesRecyclerViewAdapter;

    private Entry newEntry;
    private Entry currentEntry;
    private Long dateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry_new);

        //set requestCode to differ between editing an newEntry or creating a new one
        Intent intent = getIntent();
        requestCode = intent.getIntExtra("REQUEST_CODE", Integer.MAX_VALUE);

        //set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (requestCode == 1) setTitle("New entry");
        if (requestCode == 2) setTitle("Edit entry");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_round);
        //activate up navigation (back button on app bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set up OnClickListener for FAB
        //when clicked, open NewEntryActivity and set requestCode to NEW_ENTRY_REQUEST
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_fragment_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataViewModel viewModel = Helper.getInstance().getDataViewModel();
                if (requestCode == 1) {
                    newEntry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));

                    if (!ETbloodsugar.getText().toString().equals("")) newEntry.setBloodSugar(Integer.parseInt(ETbloodsugar.getText().toString()));
                    else newEntry.setBloodSugar(null);

                    if (!ETbreadunit.getText().toString().equals("")) newEntry.setBreadUnit(Float.parseFloat(ETbreadunit.getText().toString()));
                    else newEntry.setBreadUnit(null);

                    if (!ETbolus.getText().toString().equals("")) newEntry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    else newEntry.setBolus(null);

                    if (!ETbasal.getText().toString().equals("")) newEntry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    else newEntry.setBasal(null);

                    if (!ETnote.getText().toString().equals("")) newEntry.setNote(ETnote.getText().toString());
                    else newEntry.setNote(null);

                    viewModel.addEntry(newEntry);
                }

                if (requestCode == 2) {
                    currentEntry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));
                    if (!ETbloodsugar.getText().toString().equals("")) currentEntry.setBloodSugar(Integer.parseInt(ETbloodsugar.getText().toString()));
                    if (!ETbreadunit.getText().toString().equals("")) currentEntry.setBreadUnit(Float.parseFloat(ETbreadunit.getText().toString()));
                    if (!ETbolus.getText().toString().equals("")) currentEntry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    if (!ETbasal.getText().toString().equals("")) currentEntry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    if (!ETnote.getText().toString().equals("")) currentEntry.setNote(ETnote.getText().toString());
                    viewModel.addEntry(currentEntry);
                }

                Intent intent = new Intent(NewEntryActivity.this, MainActivity.class);
                startActivity(intent);
                finishAndRemoveTaskCustom();
            }
        });

        //get Buttons and EditTexts
        dateButton = (Button) findViewById(R.id.button_date);
        timeButton = (Button) findViewById(R.id.button_time);
        ETbloodsugar = (EditText) findViewById(R.id.editText_bloodSugar);
        ETbreadunit = (EditText) findViewById(R.id.editText_breadUnits);
        ETbolus = (EditText) findViewById(R.id.editText_bolusInjection);
        ETbasal = (EditText) findViewById(R.id.editText_basalInjection);
        ETnote = (EditText) findViewById(R.id.editText_note);
        exercisesButton = (Button) findViewById(R.id.button_exercises);
        diseasedSwitch = (Switch) findViewById(R.id.switch_diseased);


        //initialize SlidingUpPanelLayout (Slide up to show ratio wizard in NewEntryActivity)
        supl = findViewById(R.id.sliding_layout);
        slideUpTextView = (TextView) supl.findViewById(R.id.slide_up_textview);
        slideUpImageView = (ImageView) supl.findViewById(R.id.slide_up_imageView);
        supl.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //when panel state changes, adjust labels from slide "up" to slide "down" and vice versa
                if (supl.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slideUpTextView.setText("Slide down to hide ratio wizard");
                    slideUpImageView.setImageResource(R.drawable.ic_action_down);
                }
                if (supl.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slideUpTextView.setText("Slide up to show ratio wizard");
                    slideUpImageView.setImageResource(R.drawable.ic_action_up);
                }
            }
        });


        calendar = Calendar.getInstance();

        entriesRecyclerViewAdapter = Helper.getInstance().getEntriesRecyclerViewAdapter();

        //if requestCode == 1 -> new entry
        if (requestCode == 1) {
            //save the current time (when the activity was started)
            dateMillis = calendar.getTimeInMillis();
            newEntry = new Entry();
            newEntry.setTimestamp(new Timestamp(dateMillis));

            //set the text on the dateButton to current date
            dateButton.setText(Helper.formatMillisToDateString(dateMillis));

            //get the timeButton and set the text to current time
            timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
        }

        //if request code = 2 -> edit entry
        if (requestCode == 2) {
            //get entry object to update
            int position = getIntent().getIntExtra("POSITION", Integer.MAX_VALUE);
            currentEntry = entriesRecyclerViewAdapter.getItemByPosition(position);

            dateMillis = currentEntry.getTimestamp().getTime();
            calendar.setTimeInMillis(dateMillis);

            //setText to all buttons and EditTexts
            dateButton.setText(Helper.formatMillisToDateString(currentEntry.getTimestamp().getTime()));
            timeButton.setText(Helper.formatMillisToTimeString(currentEntry.getTimestamp().getTime()));

            Integer bloodsugar = currentEntry.getBloodSugar();
            if (bloodsugar != null) ETbloodsugar.setText(bloodsugar.toString());

            Float breadunit = currentEntry.getBreadUnit();
            if (breadunit != null) ETbreadunit.setText(breadunit.toString());

            Float bolus = currentEntry.getBolus();
            if (bolus != null) ETbolus.setText(bolus.toString());

            Float basal = currentEntry.getBasal();
            if (basal != null) ETbasal.setText(basal.toString());

            String note = currentEntry.getNote();
            if (note != null) ETnote.setText(note);
        }

        //set OnClickListener for dateButton to open DatePickerFragment
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putLong("dateMillis", dateMillis);
                datePickerFragment.setArguments(args);
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //set OnClickListener for timeButton to open TimePickerFragment
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putLong("dateMillis", dateMillis);
                timePickerFragment.setArguments(args);
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    //set menu (containing the actions) for the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_actions, menu);
        menu.findItem(R.id.action_refresh).setVisible(false);
        menu.findItem(R.id.action_delete_forever).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //react to action clicked on app bar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //helper function (allows to finish and remove task from inside the FAB clicklistener)
    private void finishAndRemoveTaskCustom() {
        this.finishAndRemoveTask();
    }

    //defined and called by DatePickerFragment (and nested interface)
    @Override
    public void updateDate(int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        updateLocalValues();
        dateButton.setText(Helper.formatMillisToDateString(dateMillis));
    }

    //defined and called by TimePickerFragment (and nested interface)
    @Override
    public void updateTime(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        updateLocalValues();
        timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
    }

    //helper function to update dateMillis and the newEntry values
    private void updateLocalValues() {
        this.dateMillis = calendar.getTimeInMillis();
        if (requestCode == 1) this.newEntry.setTimestamp(new Timestamp(dateMillis));
        if (requestCode == 2) this.currentEntry.setTimestamp(new Timestamp(dateMillis));
    }
}
