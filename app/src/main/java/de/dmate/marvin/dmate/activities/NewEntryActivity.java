package de.dmate.marvin.dmate.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.util.Helper;

public class NewEntryActivity extends AppCompatActivity implements
        TimePickerDialogFragment.OnTimePickerFragmentInteractionListener,
        DatePickerDialogFragment.OnDatePickerFragmentInteractionListener,
        ExercisesDialogFragment.OnExercisesDialogFragmentListener {

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
    private Entry entry;

    private List<Exercise> exercisesAll;
    private List<Exercise> exercisesOfEntry;
    private List<Sport> sportsAll;

    private Boolean entriesInitialized = false;
    private Boolean exercisesInitialized = false;

    private ExercisesDialogFragment exercisesDialogFragment;

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

        viewModel.getEntries().observe(NewEntryActivity.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                //initialize new entry
                if (requestCode == 1) {
                    if (entry == null) {
                        entry = new Entry();
                        entry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));
                        dateButton.setText(Helper.formatMillisToDateString(calendar.getTimeInMillis()));
                        timeButton.setText(Helper.formatMillisToTimeString(calendar.getTimeInMillis()));
                    }
                }
                //initialize existing entry
                if (requestCode == 2) {
                    if (entry == null) {
                        int entryId = getIntent().getIntExtra("ENTRY_ID", Integer.MAX_VALUE);
                        for (Entry e : entries) {
                            if (e.geteId().equals(entryId)) {
                                entry = e;
                            }
                        }
                        calendar.setTimeInMillis(entry.getTimestamp().getTime());
                        dateButton.setText(Helper.formatMillisToDateString(entry.getTimestamp().getTime()));
                        timeButton.setText(Helper.formatMillisToTimeString(entry.getTimestamp().getTime()));
                        if (entry.getBloodSugar() != null) ETbloodsugar.setText(entry.getBloodSugar().toString());
                        else ETbloodsugar.setText("");
                        if (entry.getBreadUnit() != null) ETbreadunit.setText(entry.getBreadUnit().toString());
                        else ETbreadunit.setText("");
                        if (entry.getBolus() != null) ETbolus.setText(entry.getBolus().toString());
                        else ETbolus.setText("");
                        if (entry.getBasal() != null) ETbasal.setText(entry.getBasal().toString());
                        else ETbasal.setText("");
                        if (entry.getNote() != null) ETnote.setText(entry.getNote());
                        else ETnote.setText("");
                        switchDiseased.setChecked(entry.getDiseased());
                    }
                }
                entriesInitialized = true;
                if (entriesInitialized && exercisesInitialized) {
                    finishInitialization();
                }
            }
        });

        viewModel.getExercises().observe(NewEntryActivity.this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                exercisesAll = exercises;
                exercisesInitialized = true;
                if (entriesInitialized && exercisesInitialized) {
                    finishInitialization();
                }
            }
        });

        viewModel.getSports().observe(NewEntryActivity.this, new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                sportsAll = sports;
                if (sportsAll == null) {
                    sportsAll = new ArrayList<>();
                }
            }
        });

        //set OnClickListener for dateButton to open DatePickerDialogFragment
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create fragment and show it
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                Bundle args = new Bundle();
                args.putLong("dateMillis", entry.getTimestamp().getTime());
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
                args.putLong("dateMillis", entry.getTimestamp().getTime());
                timePickerDialogFragment.setArguments(args);
                timePickerDialogFragment.show(getFragmentManager(), "timePicker");
            }
        });

        //set OnCheckedChangeListener for the Switch and react to switch status changes
        switchDiseased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entry.setDiseased(isChecked);
            }
        });

        //set OnClickListener for exercise button and open Dialog on click
        buttonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercisesDialogFragment = new ExercisesDialogFragment();
                exercisesDialogFragment.setExercisesOfEntry(exercisesOfEntry);
                exercisesDialogFragment.setSports(sportsAll);
                exercisesDialogFragment.show(getSupportFragmentManager(), "exercisesDialog");
            }
        });

        //set up OnClickListener for FAB
        //FAB is used to save the current entry
        FloatingActionButton fab = findViewById(R.id.fab_fragment_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new entry
                if (requestCode == 1) {
                    entry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));

                    if (!ETbloodsugar.getText().toString().equals("")) entry.setBloodSugar(Float.parseFloat(ETbloodsugar.getText().toString()));
                    else entry.setBloodSugar(null);

                    if (!ETbreadunit.getText().toString().equals("")) entry.setBreadUnit(Float.parseFloat(ETbreadunit.getText().toString()));
                    else entry.setBreadUnit(null);

                    if (!ETbolus.getText().toString().equals("")) entry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    else entry.setBolus(null);

                    if (!ETbasal.getText().toString().equals("")) entry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    else entry.setBasal(null);

                    if (!ETnote.getText().toString().equals("")) entry.setNote(ETnote.getText().toString());
                    else entry.setNote(null);

                    viewModel.addEntryWithExercises(entry, exercisesOfEntry);
                }

                //existing entry
                if (requestCode == 2) {
                    entry.setTimestamp(new Timestamp(calendar.getTimeInMillis()));
                    if (!ETbloodsugar.getText().toString().equals("")) entry.setBloodSugar(Float.parseFloat(ETbloodsugar.getText().toString()));
                    else entry.setBloodSugar(null);
                    if (!ETbreadunit.getText().toString().equals("")) entry.setBreadUnit(Float.parseFloat(ETbreadunit.getText().toString()));
                    else entry.setBreadUnit(null);
                    if (!ETbolus.getText().toString().equals("")) entry.setBolus(Float.parseFloat(ETbolus.getText().toString()));
                    else entry.setBolus(null);
                    if (!ETbasal.getText().toString().equals("")) entry.setBasal(Float.parseFloat(ETbasal.getText().toString()));
                    else entry.setBasal(null);
                    if (!ETnote.getText().toString().equals("")) entry.setNote(ETnote.getText().toString());
                    else entry.setNote(null);

                    viewModel.addEntryWithExercises(entry, exercisesOfEntry);
                }
                Intent intent = new Intent(NewEntryActivity.this, MainActivity.class);
                startActivity(intent);
                NewEntryActivity.this.finishAndRemoveTask();
            }
        });
    }

    private void finishInitialization() {
        if (exercisesOfEntry == null) {
            exercisesOfEntry = new ArrayList<>();
        }
        if (requestCode == 2) {
            for (Exercise exOfAll : exercisesAll) {
                if (entry.geteId().equals(exOfAll.geteIdF())) {
                    boolean alreadyExisting = false;
                    for (Exercise exOfEntry : exercisesOfEntry) {
                        if (exOfEntry.getExId() != null) {
                            if (exOfEntry.getExId().equals(exOfAll.getExId())) {
                                alreadyExisting = true;
                            }
                        }
                    }
                    if (!alreadyExisting) {
                        exercisesOfEntry.add(exOfAll);
                    }
                }
            }
        }
        updateButtonExercises();
    }

    //defined and called by DatePickerDialogFragment (and nested interface)
    @Override
    public void updateDate(int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateButton.setText(Helper.formatMillisToDateString(calendar.getTimeInMillis()));
    }

    //defined and called by TimePickerDialogFragment (and nested interface)
    @Override
    public void updateTime(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        timeButton.setText(Helper.formatMillisToTimeString(calendar.getTimeInMillis()));
    }

    //update buttonExercises to indicate that there are exercises
    private void updateButtonExercises() {
        buttonExercises.setText("Exercises" + " (" + exercisesOfEntry.size() + ")");
        if (exercisesOfEntry.size() > 0) buttonExercises.setBackgroundResource(R.color.colorAccent);
        else buttonExercises.setBackground(timeButton.getBackground());
    }

    @Override
    public void setExercises(List<Exercise> exercisesOfEntry) {
        this.exercisesOfEntry = exercisesOfEntry;
        updateButtonExercises();
    }
}
