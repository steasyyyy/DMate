package de.dmate.marvin.dmate.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.DatePickerFragment;
import de.dmate.marvin.dmate.fragments.TimePickerFragment;
import de.dmate.marvin.dmate.roomDatabase.Entry;
import de.dmate.marvin.dmate.roomDatabase.EntryListViewModel;
import de.dmate.marvin.dmate.roomDatabase.RecyclerViewAdapter;
import de.dmate.marvin.dmate.util.Helper;

public class NewAndUpdateEntryActivity extends AppCompatActivity
        implements TimePickerFragment.OnTimePickerFragmentInteractionListener, DatePickerFragment.OnDatePickerFragmentInteractionListener {

    private int requestCode;

    private Button dateButton;
    private Button timeButton;
    private EditText ETbloodsugar;
    private EditText ETbreadunit;
    private EditText ETbolus;
    private EditText ETbasal;
    private EditText ETnote;

    private Calendar calendar;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private Entry newEntry;
    private Entry currentEntry;
    private Long dateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

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

        //get Buttons and EditTexts
        dateButton = (Button) findViewById(R.id.button_date);
        timeButton = (Button) findViewById(R.id.button_time);
        ETbloodsugar = (EditText) findViewById(R.id.editText_bloodsugar);
        ETbreadunit = (EditText) findViewById(R.id.editText_breadunit);
        ETbolus = (EditText) findViewById(R.id.editText_bolus);
        ETbasal = (EditText) findViewById(R.id.editText_basal);
        ETnote = (EditText) findViewById(R.id.editText_note);

        calendar = Calendar.getInstance();

        recyclerViewAdapter = Helper.getInstance().getRecyclerViewAdapter();

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
            //TODO
            currentEntry = recyclerViewAdapter.getItemByPosition(position);

            dateMillis = currentEntry.getTimestamp().getTime();
            calendar.setTimeInMillis(dateMillis);

            //setText to all buttons and EditTexts
            dateButton.setText(Helper.formatMillisToDateString(currentEntry.getTimestamp().getTime()));
            timeButton.setText(Helper.formatMillisToTimeString(currentEntry.getTimestamp().getTime()));

            Integer bloodsugar = currentEntry.getBloodsugar();
            if (bloodsugar != null) ETbloodsugar.setText(bloodsugar.toString());

            Float breadunit = currentEntry.getBreadunit();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //react to action clicked on app bar
    public boolean onOptionsItemSelected(MenuItem item) {
        EntryListViewModel viewModel = ViewModelProviders.of(this).get(EntryListViewModel.class);
        switch(item.getItemId()) {
            //action_save = save changes after updating entry or creating a new one
            case R.id.action_save:
                if (requestCode == 1) {
                    newEntry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));

                    if (!ETbloodsugar.getText().toString().equals("")) newEntry.setBloodsugar(Integer.parseInt(ETbloodsugar.getText().toString()));
                    else newEntry.setBloodsugar(null);

                    if (!ETbreadunit.getText().toString().equals("")) newEntry.setBreadunit(Float.parseFloat(ETbreadunit.getText().toString()));
                    else newEntry.setBreadunit(null);

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
                    if (!ETbloodsugar.getText().toString().equals("")) currentEntry.setBloodsugar(Integer.parseInt(ETbloodsugar.getText().toString()));
                    if (!ETbreadunit.getText().toString().equals("")) currentEntry.setBreadunit(Float.parseFloat(ETbreadunit.getText().toString()));
                    if (!ETbolus.getText().toString().equals("")) currentEntry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    if (!ETbasal.getText().toString().equals("")) currentEntry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    if (!ETnote.getText().toString().equals("")) currentEntry.setNote(ETnote.getText().toString());
                    viewModel.updateEntry(currentEntry);
                }

                Intent intent = new Intent(NewAndUpdateEntryActivity.this, MainActivity.class);
                startActivity(intent);
                this.finishAndRemoveTask();
                return true;

            //action for up navigation (basic back button on app bar)
            case android.R.id.home :
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
