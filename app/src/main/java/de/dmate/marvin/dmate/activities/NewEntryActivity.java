package de.dmate.marvin.dmate.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.OtherFragments.ExercisesDialogFragment;
import de.dmate.marvin.dmate.fragments.PickerDialogFragments.DatePickerDialogFragment;
import de.dmate.marvin.dmate.fragments.PickerDialogFragments.TimePickerDialogFragment;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;
import de.dmate.marvin.dmate.util.Helper;

public class NewEntryActivity extends AppCompatActivity implements
        TimePickerDialogFragment.OnTimePickerFragmentInteractionListener,
        DatePickerDialogFragment.OnDatePickerFragmentInteractionListener,
        ExercisesDialogFragment.OnExercisesDialogFragmentListener, UpdateRecommendationsAsyncTask.AsyncResponseListener {

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
    private TextView supUsualBUF;
    private TextView supUsualBUDose;
    private TextView supRecBUF;
    private TextView supRecBUDose;
    private Button supAccept;

    private Calendar calendar;
    private DataViewModel viewModel;
    private Entry entry;
    private Daytime daytime;
    private User user;

    private List<Exercise> exercisesAll;
    private List<Exercise> exercisesOfEntry;
    private List<Sport> sportsAll;
    private List<Daytime> daytimesAll;

    private Boolean entriesInitialized = false;
    private Boolean exercisesInitialized = false;
    private Boolean sportsInitialized = false;
    private Boolean daytimesInitialized = false;
    private Boolean userInitialized = false;

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
                if (supl.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slideUpTextView.setText("Slide down to hide ratio wizard");
                    slideUpImageView.setImageResource(R.drawable.ic_action_down);
                    if (daytimesInitialized && entriesInitialized && exercisesInitialized && sportsInitialized && userInitialized) {
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

                        new UpdateRecommendationsAsyncTask(NewEntryActivity.this, daytimesAll, entry, user, sportsAll, exercisesOfEntry).execute();
                    }
                }
                if (supl.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slideUpTextView.setText("Slide up to show ratio wizard");
                    slideUpImageView.setImageResource(R.drawable.ic_action_up);
                }
            }
        });
        supUsualBUF = findViewById(R.id.textView_sup_bu_factor_usual);
        supUsualBUDose = findViewById(R.id.textView_sup_bolus_usual);
        supRecBUF = findViewById(R.id.textView_sup_bu_factor_consulting);
        supRecBUDose = findViewById(R.id.textView_sup_bolus_consulting);
        supAccept = findViewById(R.id.button_sup_accept);
        supAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supUsualBUF.getText().toString().equals("-")
                        || supUsualBUDose.getText().toString().equals("-")
                        || supRecBUF.getText().toString().equals("-")
                        || supRecBUDose.getText().toString().equals("-")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to accept recommendations as they are not complete", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    if (!supRecBUDose.getText().toString().equals("-")) {
                        entry.setBolus(Float.parseFloat(supRecBUDose.getText().toString()));
                        ETbolus.setText(supRecBUDose.getText().toString());
                        Toast toast = Toast.makeText(getApplicationContext(), "Recommendation accepted", Toast.LENGTH_LONG);
                        toast.show();
                        supl.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
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
                if (daytimesInitialized && entriesInitialized && exercisesInitialized && sportsInitialized && userInitialized) {
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

                    new UpdateRecommendationsAsyncTask(NewEntryActivity.this, daytimesAll, entry, user, sportsAll, exercisesOfEntry).execute();
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
                if (daytimesInitialized && entriesInitialized && exercisesInitialized && sportsInitialized && userInitialized) {
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

                    new UpdateRecommendationsAsyncTask(NewEntryActivity.this, daytimesAll, entry, user, sportsAll, exercisesOfEntry).execute();
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
                sportsInitialized = true;
                if (daytimesInitialized && entriesInitialized && exercisesInitialized && sportsInitialized && userInitialized) {
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

                    new UpdateRecommendationsAsyncTask(NewEntryActivity.this, daytimesAll, entry, user, sportsAll, exercisesOfEntry).execute();
                }
            }
        });

        viewModel.getDaytimes().observe(NewEntryActivity.this, new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                daytimesAll = daytimes;
                daytimesInitialized = true;
                if (daytimesInitialized && entriesInitialized && exercisesInitialized && sportsInitialized && userInitialized) {
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

                    new UpdateRecommendationsAsyncTask(NewEntryActivity.this, daytimesAll, entry, user, sportsAll, exercisesOfEntry).execute();
                }
            }
        });

        viewModel.getUsers().observe(NewEntryActivity.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                try {
                    user = users.get(0);
                } catch (IndexOutOfBoundsException e) {
                    user = new User();
                    viewModel.addUser(user);
                    Toast toast = Toast.makeText(getApplicationContext(), "Created new user", Toast.LENGTH_LONG);
                    toast.show();
                }
                userInitialized = true;
                if (daytimesInitialized && entriesInitialized && exercisesInitialized && sportsInitialized && userInitialized) {
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

                    new UpdateRecommendationsAsyncTask(NewEntryActivity.this, daytimesAll, entry, user, sportsAll, exercisesOfEntry).execute();
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

    @Override
    public void handleResponseFromAsyncTask(Map<String, Float> map) {
        if (map.get("usualBuFactor") == 0f
                || map.get("usualBolusInsulinDose") == 0f
                || map.get("recBuFactor") == 0f
                || map.get("recBolusInsulinDose") == 0f) {
            Toast toast = Toast.makeText(getApplicationContext(), "Unable to finish calculations. See notifications for further detail and/or add more entries for the ratio wizard to work properly.", Toast.LENGTH_LONG);
            toast.show();
        }
        if (map.get("usualBuFactor") != 0f) supUsualBUF.setText(map.get("usualBuFactor").toString());
        if (map.get("usualBolusInsulinDose") != 0f) supUsualBUDose.setText(map.get("usualBolusInsulinDose").toString());
        if (map.get("recBuFactor") != 0f) supRecBUF.setText(map.get("recBuFactor").toString());
        if (map.get("recBolusInsulinDose") != 0f) supRecBUDose.setText(map.get("recBolusInsulinDose").toString());
    }
}

class UpdateRecommendationsAsyncTask extends AsyncTask<Void, Void, Map<String, Float>> {

    private final AsyncResponseListener asyncResponseListener;
    private final List<Daytime> daytimes;
    private final List<Sport> sports;
    private final List<Exercise> exercises;
    private final Entry entry;
    private final User user;
    private Daytime daytime;

    public interface AsyncResponseListener {
        void handleResponseFromAsyncTask(Map<String, Float> map);
    }

    UpdateRecommendationsAsyncTask(AsyncResponseListener asyncResponseListener, List<Daytime> daytimes, Entry entry, User user, List<Sport> sports, List<Exercise> exercises) {
        this.asyncResponseListener = asyncResponseListener;
        this.daytimes = daytimes;
        this.entry = entry;
        this.user = user;
        this.sports = sports;
        this.exercises = exercises;
        if (daytimes == null) System.out.println("DAYTIMES NULL");
        if (entry == null) System.out.println("ENTRY NULL");
        if (user == null) System.out.println("USER NULL");
        if (sports == null) System.out.println("SPORTS NULL");
        if (exercises == null) System.out.println("EXERCISES NULL");
    }

    @Override
    protected Map<String, Float> doInBackground(Void... voids) {
        Map<String, Float> map = new HashMap<>();
        map.put("usualBuFactor", 0f);
        map.put("usualBolusInsulinDose", 0f);
        map.put("recBuFactor", 0f);
        map.put("recBolusInsulinDose", 0f);

        for (Daytime d : daytimes) {
            Timestamp daytimeStart = Helper.getTimestampFromTimeString(d.getDaytimeStart());
            Timestamp daytimeEnd = Helper.getTimestampFromTimeString(d.getDaytimeEnd());
            String entryTimeString = Helper.formatMillisToTimeString(entry.getTimestamp().getTime());
            Timestamp cleanedTimestamp = Helper.getTimestampFromTimeString(entryTimeString);

            if (cleanedTimestamp.after(daytimeStart) && cleanedTimestamp.before(daytimeEnd)) {
                entry.setdIdF(d.getdId());
                daytime = d;
            }
        }

        if (daytime != null) {
            map.put("usualBuFactor", daytime.getBuFactor());
            if (daytime.getBuFactorConsultingArithMean() != null) map.put("recBuFactor", daytime.getBuFactorConsultingArithMean());
            if (entry.getBreadUnit() != null) map.put("usualBolusInsulinDose", daytime.getBuFactor() * entry.getBreadUnit());

            //calc divergence from target
            Float targetMin = user.getTargetMin();
            Float targetMax = user.getTargetMax();
            if (targetMin != null && targetMax != null) {
                if (entry.getBloodSugar() != null) {
                    Float divergenceFromTarget = 0f;
                    if (entry.getBloodSugar() < targetMin) {
                        divergenceFromTarget = entry.getBloodSugar() - targetMin;
                    }
                    if (entry.getBloodSugar() > targetMax) {
                        divergenceFromTarget = entry.getBloodSugar() - targetMax;
                    }
                    entry.setDivergenceFromTarget(divergenceFromTarget);
                }
            }
            //calc correction for high/low blood sugar values
            if (entry.getDivergenceFromTarget() != null) {
                Float correctionBS = 0f;
                if (!(entry.getDivergenceFromTarget().equals(0f))) {
                    if (entry.getDivergenceFromTarget() > 0) correctionBS = (float)Math.ceil(entry.getDivergenceFromTarget()/daytime.getCorrectionFactor());
                    if (entry.getDivergenceFromTarget() < 0) correctionBS = (float)Math.floor(entry.getDivergenceFromTarget()/daytime.getCorrectionFactor());
                }
                if (!(correctionBS.equals(0f))) {
                    entry.setBolusCorrectionByBloodSugar(correctionBS);
                }
            }
            //calc correction for exercises
            if (exercises.size() > 0) {
                Float effectSum = 0f;
                for (Exercise ex : exercises) {
                    for (Sport s : sports) {
                        if (s.getsId().equals(ex.getsIdF())) {
                            effectSum += ex.getExerciseUnits() * s.getSportEffectPerUnit();
                        }
                    }
                }
                Float correctionSport = 0f;
                if (effectSum > 0) {
                    effectSum = effectSum * (-1f);
                    correctionSport = (float)Math.floor(effectSum/daytime.getCorrectionFactor());
                }
                if (!(correctionSport.equals(0f))) {
                    entry.setBolusCorrectionBySport(correctionSport);
                }
            }

            Float recBolusDose = 0f;
            if (entry.getBolusCorrectionByBloodSugar() != null) {
                recBolusDose = entry.getBolusCorrectionByBloodSugar();
            }
            if (entry.getBolusCorrectionBySport() != null) {
                recBolusDose = entry.getBolusCorrectionBySport();
            }
            if (entry.getBolusCorrectionByBloodSugar() != null && entry.getBolusCorrectionBySport() != null) {
                recBolusDose = entry.getBolusCorrectionByBloodSugar() + entry.getBolusCorrectionBySport();
            }
            if (entry.getBreadUnit() != null) {
                if (map.get("recBuFactor") != 0f) {
                    recBolusDose += entry.getBreadUnit() * map.get("recBuFactor");
                } else {
                    recBolusDose = 0f;
                }
            }
            map.put("recBolusInsulinDose", recBolusDose);
        }
        map.put("usualBuFactor", round(map.get("usualBuFactor"), 2));
        map.put("usualBolusInsulinDose", round(map.get("usualBolusInsulinDose"), 0));
        map.put("recBuFactor", round(map.get("recBuFactor"), 2));
        map.put("recBolusInsulinDose", round(map.get("recBolusInsulinDose"), 0));
        return map;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    protected void onPostExecute(Map<String, Float> map) {
        super.onPostExecute(map);
        asyncResponseListener.handleResponseFromAsyncTask(map);
        System.out.println("Usual bu factor: " + map.get("usualBuFactor"));
        System.out.println("Usual bolus insulin dose: " + map.get("usualBolusInsulinDose"));
        System.out.println("Recommended bu factor: " + map.get("recBuFactor"));
        System.out.println("Recommended bolus insulin dose: " + map.get("recBolusInsulinDose"));
    }
}
