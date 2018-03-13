package de.dmate.marvin.dmate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.entities.Entry;
import de.dmate.marvin.dmate.fragments.DatePickerFragment;
import de.dmate.marvin.dmate.fragments.TimePickerFragment;
import de.dmate.marvin.dmate.util.Helper;

public class NewAndUpdateEntryActivity extends AppCompatActivity
        implements TimePickerFragment.OnTimePickerFragmentInteractionListener, DatePickerFragment.OnDatePickerFragmentInteractionListener {

    public static final int ENTRY_CREATION_SUCCESSFUL = 1;
    public static final int ENTRY_CREATION_CANCELLED = -1;
    public static final int ENTRY_EDIT_SUCCESSFUL = 2;
    public static final int ENTRY_EDIT_CANCELLED = -2;

    private int requestCode;
    private int position;

    private Button dateButton;
    private Button timeButton;
    private EditText ETbloodsugar;
    private EditText ETbreadunit;
    private EditText ETbolus;
    private EditText ETbasal;
    private EditText ETnote;

    private Calendar calendar;

    private Entry.EntryBuilder newEntry;
    private Entry currentEntry;
    private Long dateMillis;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

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

        //get Buttons and EditTexts
        dateButton = (Button) findViewById(R.id.button_date);
        timeButton = (Button) findViewById(R.id.button_time);
        ETbloodsugar = (EditText) findViewById(R.id.editText_bloodsugar);
        ETbreadunit = (EditText) findViewById(R.id.editText_breadunit);
        ETbolus = (EditText) findViewById(R.id.editText_bolus);
        ETbasal = (EditText) findViewById(R.id.editText_basal);
        ETnote = (EditText) findViewById(R.id.editText_note);

        calendar = Calendar.getInstance();

        //if requestCode == 1 -> new entry
        if (requestCode == 1) {
            //save the current time (when the activity was started)
            dateMillis = calendar.getTimeInMillis();
            newEntry = Entry.dateMillis(dateMillis);

            //set the text on the dateButton to current date
            dateButton.setText(Helper.formatMillisToDateString(dateMillis));

            //get the timeButton and set the text to current time
            timeButton.setText(Helper.formatMillisToTimeString(dateMillis));
        }

        //if request code = 2 -> edit entry
        if (requestCode == 2) {
            //get entry object to update
            position = getIntent().getIntExtra("POSITION", Integer.MAX_VALUE);
            currentEntry = Helper.getInstance().getApplication().getEntry(position);

            dateMillis = currentEntry.getDateMillis();
            calendar.setTimeInMillis(dateMillis);

            //setText to all buttons and EditTexts
            dateButton.setText(Helper.formatMillisToDateString(currentEntry.getDateMillis()));
            timeButton.setText(Helper.formatMillisToTimeString(currentEntry.getDateMillis()));

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //react to action clicked on app bar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //action_save = save changes after updating entry or creating a new one
            case R.id.action_save:
                if (requestCode == 1) {
                    newEntry.dateMillis(calendar.getTimeInMillis());
                    if (!ETbloodsugar.getText().toString().equals("")) newEntry.bloodsugar(Integer.parseInt(ETbloodsugar.getText().toString()));
                    if (!ETbreadunit.getText().toString().equals("")) newEntry.breadunit(Float.parseFloat(ETbreadunit.getText().toString()));
                    if (!ETbolus.getText().toString().equals("")) newEntry.bolus(Float.parseFloat(ETbolus.getText().toString()));
                    if (!ETbasal.getText().toString().equals("")) newEntry.basal(Float.parseFloat(ETbasal.getText().toString()));
                    if (!ETnote.getText().toString().equals("")) newEntry.note(ETnote.getText().toString());
                    newEntry.build();
                }

                if (requestCode == 2) {
                    currentEntry.setDateMillis(calendar.getTimeInMillis());
                    if (!ETbloodsugar.getText().toString().equals("")) currentEntry.setBloodsugar(Integer.parseInt(ETbloodsugar.getText().toString()));
                    if (!ETbreadunit.getText().toString().equals("")) currentEntry.setBreadunit(Float.parseFloat(ETbreadunit.getText().toString()));
                    if (!ETbolus.getText().toString().equals("")) currentEntry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    if (!ETbasal.getText().toString().equals("")) currentEntry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    if (!ETnote.getText().toString().equals("")) currentEntry.setNote(ETnote.getText().toString());
                }

                Intent intent = new Intent(NewAndUpdateEntryActivity.this, MainActivity.class);
                startActivity(intent);
                this.finishAndRemoveTask();
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
        this.newEntry.dateMillis(dateMillis);
    }
}
