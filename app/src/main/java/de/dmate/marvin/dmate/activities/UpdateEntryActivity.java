package de.dmate.marvin.dmate.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.fragments.DatePickerFragment;
import de.dmate.marvin.dmate.fragments.TimePickerFragment;
import de.dmate.marvin.dmate.util.Helper;

public class UpdateEntryActivity extends AppCompatActivity implements TimePickerFragment.OnFragmentInteractionListener, DatePickerFragment.OnFragmentInteractionListener {

    private Entry.EntryBuilder entry;
    private Long dateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_entry);

        //set up calendar to save the current time (when the activity was started)
        final Calendar calendar = Calendar.getInstance();
        dateMillis = calendar.getTimeInMillis();
        entry = Entry.dateMillis(dateMillis);

        Button dateButton = (Button) findViewById(R.id.button_date);
        dateButton.setText(Helper.formatMillisToDateString(dateMillis));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button timeButton = (Button) findViewById(R.id.button_time);
        timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
