package de.dmate.marvin.dmate.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.PickerDialogFragments.DatePickerDialogFragment;
import de.dmate.marvin.dmate.fragments.PickerDialogFragments.TimePickerDialogFragment;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.util.EntriesRecyclerViewAdapter;
import de.dmate.marvin.dmate.util.Helper;

public class NewEntryActivity extends AppCompatActivity implements
        TimePickerDialogFragment.OnTimePickerFragmentInteractionListener,
        DatePickerDialogFragment.OnDatePickerFragmentInteractionListener,
        ExercisesDialogFragment.OnExercisesDialogFragmentListener, DialogInterface.OnDismissListener {

    private int requestCode;

    private Button dateButton;
    private Button timeButton;
    private EditText ETbloodsugar;
    private EditText ETbreadunit;
    private EditText ETbolus;
    private EditText ETbasal;
    private EditText ETnote;
    private Button buttonExercises;
    private Switch switchDiseased;

    private SlidingUpPanelLayout supl;
    private TextView slideUpTextView;
    private ImageView slideUpImageView;

    private Calendar calendar;

    private DataViewModel viewModel;

    private EntriesRecyclerViewAdapter entriesRecyclerViewAdapter;

    private Entry newEntry;
    private Entry currentEntry;
    private Long dateMillis;

    private List<Exercise> exercisesInitial;
    private Boolean exercisesInitialized = false;

    private ExercisesDialogFragment exercisesDialogFragment;

    //TODO following process still cause bugs:
    //create a new entry, add exercises, save them and save the entry
    //reopen the entry from HomeFragment
    //open exercises dialog, add a new entry
    //instead of only the new entry being added, the two existing entries are added again

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        //set requestCode to differ between editing an newEntry or creating a new one
        Intent intent = getIntent();
        requestCode = intent.getIntExtra("REQUEST_CODE", Integer.MAX_VALUE);

        //set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (requestCode == 1) setTitle("New entry");
        if (requestCode == 2) setTitle("Edit entry");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_round);
        //activate up navigation (back button on app bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get Buttons and EditTexts
        dateButton = findViewById(R.id.button_date);
        timeButton = findViewById(R.id.button_time);
        ETbloodsugar = findViewById(R.id.editText_bloodSugar);
        ETbreadunit = findViewById(R.id.editText_breadUnits);
        ETbolus = findViewById(R.id.editText_bolusInjection);
        ETbasal = findViewById(R.id.editText_basalInjection);
        ETnote = findViewById(R.id.editText_note);
        buttonExercises = findViewById(R.id.button_exercises);
        switchDiseased = findViewById(R.id.switch_diseased);

        //initialize SlidingUpPanelLayout (Slide up to show ratio wizard in NewEntryActivity)
        supl = findViewById(R.id.sliding_layout);
        slideUpTextView = supl.findViewById(R.id.slide_up_textview);
        slideUpImageView = supl.findViewById(R.id.slide_up_imageView);
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

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        entriesRecyclerViewAdapter = Helper.getInstance().getEntriesRecyclerViewAdapter();

        viewModel.getExercises().observe(NewEntryActivity.this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {

                if (!exercisesInitialized) {
                    exercisesInitial = new ArrayList<>(exercises);
                    exercisesInitialized = true;
                }

                if (requestCode == 2) {
                    for (Exercise ex : exercises) {
                        if (ex.geteIdF() != null && ex.geteIdF().intValue() == currentEntry.geteId()) {
                            if (!currentEntry.getExercises().contains(ex)) currentEntry.getExercises().add(ex);
                            exercisesInitial.add(ex);
                        }
                    }
                }
                updateButtonExercises();
            }
        });

        //if requestCode == 1 -> initialize new entry
        if (requestCode == 1) {
            //save the current time (when the activity was started)
            dateMillis = calendar.getTimeInMillis();
            newEntry = new Entry();
            newEntry.setTimestamp(new Timestamp(dateMillis));

            //set the text on the dateButton to current date
            dateButton.setText(Helper.formatMillisToDateString(dateMillis));

            //get the timeButton and set the text to current time
            timeButton.setText(Helper.formatMillisToTimeString(dateMillis));

            switchDiseased.setChecked(false);
        }

        //if request code = 2 -> initialize existing entry
        if (requestCode == 2) {
            //get entry object to update
            int position = getIntent().getIntExtra("POSITION", Integer.MAX_VALUE);
            currentEntry = entriesRecyclerViewAdapter.getItemByPosition(position);

            dateMillis = currentEntry.getTimestamp().getTime();
            calendar.setTimeInMillis(dateMillis);

            //setText to all buttons and EditTexts
            dateButton.setText(Helper.formatMillisToDateString(currentEntry.getTimestamp().getTime()));
            timeButton.setText(Helper.formatMillisToTimeString(currentEntry.getTimestamp().getTime()));

            Float bloodsugar = currentEntry.getBloodSugar();
            if (bloodsugar != null) ETbloodsugar.setText(bloodsugar.toString());

            Float breadunit = currentEntry.getBreadUnit();
            if (breadunit != null) ETbreadunit.setText(breadunit.toString());

            Float bolus = currentEntry.getBolus();
            if (bolus != null) ETbolus.setText(bolus.toString());

            Float basal = currentEntry.getBasal();
            if (basal != null) ETbasal.setText(basal.toString());

            String note = currentEntry.getNote();
            if (note != null) ETnote.setText(note);

            Boolean diseased = currentEntry.getReliable();
            switchDiseased.setChecked(diseased);
        }

        //set OnClickListener for dateButton to open DatePickerDialogFragment
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                Bundle args = new Bundle();
                args.putLong("dateMillis", dateMillis);
                datePickerDialogFragment.setArguments(args);
                datePickerDialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //set OnClickListener for timeButton to open TimePickerDialogFragment
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                Bundle args = new Bundle();
                args.putLong("dateMillis", dateMillis);
                timePickerDialogFragment.setArguments(args);
                timePickerDialogFragment.show(getFragmentManager(), "timePicker");
            }
        });

        //set OnCheckedChangeListener for the Switch and react to switch status changes
        switchDiseased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(requestCode == 1) newEntry.setReliable(isChecked);
                if (requestCode == 2) currentEntry.setReliable(isChecked);
            }
        });

        //set OnClickListener for exercise button and open Dialog on click
        buttonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercisesDialogFragment = new ExercisesDialogFragment();
                if (requestCode == 1) exercisesDialogFragment.setEntry(newEntry);
                if(requestCode == 2) exercisesDialogFragment.setEntry(currentEntry);

                exercisesDialogFragment.show(getSupportFragmentManager(), "exercisesDialog");
            }
        });

        //set up OnClickListener for FAB
        //FAB is used to save the current entry
        FloatingActionButton fab = findViewById(R.id.fab_fragment_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestCode == 1) {
                    newEntry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));

                    if (!ETbloodsugar.getText().toString().equals("")) newEntry.setBloodSugar(Float.parseFloat(ETbloodsugar.getText().toString()));
                    else newEntry.setBloodSugar(null);

                    if (!ETbreadunit.getText().toString().equals("")) newEntry.setBreadUnit(Float.parseFloat(ETbreadunit.getText().toString()));
                    else newEntry.setBreadUnit(null);

                    if (!ETbolus.getText().toString().equals("")) newEntry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    else newEntry.setBolus(null);

                    if (!ETbasal.getText().toString().equals("")) newEntry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    else newEntry.setBasal(null);

                    if (!ETnote.getText().toString().equals("")) newEntry.setNote(ETnote.getText().toString());
                    else newEntry.setNote(null);

                    List<Exercise> exercisesToAdd = new ArrayList<>();
                    for (Exercise e : newEntry.getExercises()) {
                        if (!exercisesInitial.contains(e)) {
                            exercisesToAdd.add(e);
                            exercisesInitial.add(e);
                        }
                    }
                    viewModel.addEntryWithExercises(newEntry, exercisesToAdd);
                }

                if (requestCode == 2) {
                    currentEntry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));
                    if (!ETbloodsugar.getText().toString().equals("")) currentEntry.setBloodSugar(Float.parseFloat(ETbloodsugar.getText().toString()));
                    if (!ETbreadunit.getText().toString().equals("")) currentEntry.setBreadUnit(Float.parseFloat(ETbreadunit.getText().toString()));
                    if (!ETbolus.getText().toString().equals("")) currentEntry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    if (!ETbasal.getText().toString().equals("")) currentEntry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    if (!ETnote.getText().toString().equals("")) currentEntry.setNote(ETnote.getText().toString());

                    List<Exercise> exercisesToAdd = new ArrayList<>();
                    for (Exercise e : currentEntry.getExercises()) {
                        if (!exercisesInitial.contains(e)) {
                            exercisesToAdd.add(e);
                            exercisesInitial.add(e);
                        }
                    }
                    viewModel.addEntryWithExercises(currentEntry, exercisesToAdd);
                }

                Intent intent = new Intent(NewEntryActivity.this, MainActivity.class);
                startActivity(intent);
                NewEntryActivity.this.finishAndRemoveTask();
            }
        });
    }

    //defined and called by DatePickerDialogFragment (and nested interface)
    @Override
    public void updateDate(int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        updateLocalValues();
        dateButton.setText(Helper.formatMillisToDateString(dateMillis));
    }

    //defined and called by TimePickerDialogFragment (and nested interface)
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

    //called when (or slightly before?) ExercisesDialogFragment is dismissed
    @Override
    public void onDismiss(DialogInterface dialog) {
        //check if new exercises were added
        if (requestCode == 1) this.newEntry = exercisesDialogFragment.getEntry();
        if (requestCode == 2) this.currentEntry = exercisesDialogFragment.getEntry();
        updateButtonExercises();

    }

    //update buttonExercises to indicate that there are exercises
    private void updateButtonExercises() {
        if (requestCode == 1) {
            buttonExercises.setText("Exercises" + " (" + newEntry.getExercises().size() + ")");
            if (newEntry.getExercises().size() > 0) buttonExercises.setBackgroundResource(R.color.colorAccent);
            else buttonExercises.setBackground(timeButton.getBackground());
        }
        if (requestCode == 2) {
            buttonExercises.setText("Exercises" + " (" + currentEntry.getExercises().size() + ")");
            if (currentEntry.getExercises().size() > 0) buttonExercises.setBackgroundResource(R.color.colorAccent);
            else buttonExercises.setBackground(timeButton.getBackground());
        }
    }
}
