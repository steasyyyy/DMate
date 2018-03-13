package de.dmate.marvin.dmate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.fragments.DatePickerFragment;
import de.dmate.marvin.dmate.fragments.TimePickerFragment;
import de.dmate.marvin.dmate.util.Helper;

public class NewEntryActivity extends AppCompatActivity
        implements TimePickerFragment.OnTimePickerFragmentInteractionListener, DatePickerFragment.OnDatePickerFragmentInteractionListener {

    private Button dateButton;
    private Button timeButton;
    private Calendar calendar;

    private Entry.EntryBuilder entry;
    private Long dateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("New Entry");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));

        //set up calendar to save the current time (when the activity was started)
        calendar = Calendar.getInstance();
        dateMillis = calendar.getTimeInMillis();
        entry = Entry.dateMillis(dateMillis);

        //get dateButton and set the text to current date
        dateButton = (Button) findViewById(R.id.button_date);
        dateButton.setText(Helper.formatMillisToDateString(dateMillis));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //get the timeButton and set the text to current time
        timeButton = (Button) findViewById(R.id.button_time);
        timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
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

    //helper function to update dateMillis and the entry values
    private void updateLocalValues() {
        this.dateMillis = calendar.getTimeInMillis();
        this.entry.dateMillis(dateMillis);
    }
}
