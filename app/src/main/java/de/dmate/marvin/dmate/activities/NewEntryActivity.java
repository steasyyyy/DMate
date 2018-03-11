package de.dmate.marvin.dmate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

        //set up calendar to save the current time (when the activity was started)
        calendar = Calendar.getInstance();
        dateMillis = calendar.getTimeInMillis();
        entry = Entry.dateMillis(dateMillis);

        dateButton = (Button) findViewById(R.id.button_date);
        dateButton.setText(Helper.formatMillisToDateString(dateMillis));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        timeButton = (Button) findViewById(R.id.button_time);
        timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public void updateDate(int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        updateDateTime();
        dateButton.setText(Helper.formatMillisToDateString(dateMillis));
    }

    @Override
    public void updateTime(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        updateDateTime();
        timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
    }

    public void setDateMillis(Long dateMillis) {
        this.dateMillis = dateMillis;
    }

    private void updateDateTime() {
        this.dateMillis = calendar.getTimeInMillis();
        this.entry.dateMillis(dateMillis);
    }
}
