package de.dmate.marvin.dmate.activities;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;

public class ExercisesDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private OnExercisesDialogFragmentListener mListener;

    private ListView listViewExercises;
    private Button buttonNewExercise;
    private RelativeLayout relativeLayoutNewExercise;
    private Spinner spinnerSelectSport;
    private EditText editTextUnits;
    private Button buttonCancelNewExercise;
    private Button buttonConfirmNewExercise;
    private Button buttonConfirmExercises;

    private DataViewModel viewModel;

    private ExercisesArrayAdapter exercisesAdapter;
    private SpinnerArrayAdapter spinnerAdapter;

    //"entry" is set before showing this DialogFragment in NewEntryActivity
    // ID might be null though, if it's a new entry that has not been written to the database yet
    private Entry entry;
    private List<Sport> sports;
    private List<Exercise> exercises;

    private Boolean exercisesInitialized = false;
    private List<Exercise> initialExercises;

    public ExercisesDialogFragment() {

    }

    public static ExercisesDialogFragment newInstance() {
        ExercisesDialogFragment fragment = new ExercisesDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //set new View to the builder and create the Dialog
        Dialog dialog = builder.setView(new View(getActivity())).create();

        //get WindowManager.LayoutParams, copy attributes from Dialog to LayoutParams and override them with MATCH_PARENT
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //show the Dialog before setting new LayoutParams to the Dialog
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_entry_dialog_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        listViewExercises = view.findViewById(R.id.listView_exercises);
        buttonNewExercise = view.findViewById(R.id.button_new_exercise);
        relativeLayoutNewExercise = view.findViewById(R.id.relativeLayout_new_exercise);
        spinnerSelectSport = view.findViewById(R.id.spinner_select_sport);
        editTextUnits = view.findViewById(R.id.editText_exercise_units);
        buttonCancelNewExercise = view.findViewById(R.id.button_cancel_new_exercise);
        buttonConfirmNewExercise = view.findViewById(R.id.button_confirm_new_exercise);
        buttonConfirmExercises = view.findViewById(R.id.button_confirm_exercises);

        exercisesAdapter = new ExercisesArrayAdapter(getContext(), new ArrayList<Exercise>());
        exercisesAdapter.setSpinner(spinnerSelectSport);
        listViewExercises.setAdapter(exercisesAdapter);
        listViewExercises.setOnItemClickListener(this);
        registerForContextMenu(listViewExercises);

        spinnerAdapter = new SpinnerArrayAdapter(getContext(), new ArrayList<Sport>());
        spinnerSelectSport.setAdapter(spinnerAdapter);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        viewModel.getSports().observe(ExercisesDialogFragment.this, new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                ExercisesDialogFragment.this.sports = sports;
                exercisesAdapter.setSports(sports);
                spinnerAdapter.updateSports(sports);
            }
        });

        viewModel.getExercises().observe(ExercisesDialogFragment.this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {

                //save initial state of exercises if it did not already happen
                if (!exercisesInitialized) {
                    initialExercises = exercises;
                    exercisesInitialized = true;
                }

                //if there is an exercise that did not exist during initialization, it must have been newly added
                //so add it to the current entry
                for (Exercise exercise : entry.getExercises()) {
                    if (!entry.getExercises().contains(exercise)) {
                        entry.getExercises().add(exercise);
                        //add newly added exercise to initialExercises as it should not be added again on the next change
                        initialExercises.add(exercise);
                    }
                }
                exercisesAdapter.updateExercises(entry.getExercises());
            }
        });

        buttonNewExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmExercises.setVisibility(View.GONE);
                buttonNewExercise.setVisibility(View.GONE);
                relativeLayoutNewExercise.setVisibility(View.VISIBLE);
            }
        });

        buttonCancelNewExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmExercises.setVisibility(View.VISIBLE);
                buttonNewExercise.setVisibility(View.VISIBLE);
                relativeLayoutNewExercise.setVisibility(View.GONE);

                spinnerSelectSport.setSelection(0);
                editTextUnits.setText("");
            }
        });

        buttonConfirmNewExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerSelectSport.getSelectedItem() == null) {
                    Toast toast = Toast.makeText(getContext(), "Please select a sportive activity or create a new one in settings first", Toast.LENGTH_LONG);
                    toast.show();
                }

                if (editTextUnits.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define the number of units you are going to exercise", Toast.LENGTH_LONG);
                    toast.show();
                }

                Exercise exercise = new Exercise();
                exercise.setsIdF(((Sport)spinnerSelectSport.getSelectedItem()).getsId());
                exercise.setExerciseUnits(Float.parseFloat(editTextUnits.getText().toString()));

                if (!entry.getExercises().contains(exercise)) entry.getExercises().add(exercise);
                viewModel.addExercise(exercise);

                buttonConfirmExercises.setVisibility(View.VISIBLE);
                buttonNewExercise.setVisibility(View.VISIBLE);
                relativeLayoutNewExercise.setVisibility(View.GONE);

                spinnerSelectSport.setSelection(0);
                editTextUnits.setText("");

                Toast toast = Toast.makeText(getContext(), "New exercise added", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        buttonConfirmExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "Exercises updated", Toast.LENGTH_LONG);
                toast.show();
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExercisesDialogFragmentListener) {
            mListener = (OnExercisesDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExercisesDialogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Exercise exercise = exercisesAdapter.getItem(position);
        Sport sport = null;

        for (Sport s : sports) {
            if (s.getsId().equals(exercise.getsIdF())) {
                sport = s;
                break;
            }
        }

        if (sport != null) {
            spinnerSelectSport.setSelection(spinnerAdapter.getPosition(sport));
        }

        editTextUnits.setText(exercise.getExerciseUnits().toString());

        buttonConfirmExercises.setVisibility(View.GONE);
        buttonNewExercise.setVisibility(View.GONE);
        relativeLayoutNewExercise.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Exercise exercise = exercisesAdapter.getItem(info.position);
                viewModel.deleteExercise(exercise);
                entry.getExercises().remove(exercise);
                exercisesAdapter.remove(exercise);
                Toast toast = Toast.makeText(getContext(), "Exercise deleted", Toast.LENGTH_LONG);
                toast.show();
                return true;
            }
        });
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Entry getEntry() {
        return this.entry;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
        super.onDismiss(dialog);
    }

    public interface OnExercisesDialogFragmentListener {

    }
}

class ExercisesArrayAdapter extends ArrayAdapter<Exercise> {

    private Spinner spinner;
    private List<Sport> sports;

    public ExercisesArrayAdapter(@NonNull Context context, List<Exercise> exercises) {
        super(context, 0, exercises);
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    public void updateExercises(List<Exercise> exercises) {
        this.clear();
        this.addAll(exercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (getItem(position) != null) {
            Exercise exercise = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_exercise, parent, false);
            }

            Sport sport = null;
            for (Sport s : sports) {
                if (s.getsId().intValue() == exercise.getsIdF().intValue()) {
                    sport = s;
                }
            }

            TextView textViewSportName = convertView.findViewById(R.id.textView_exercise_sport_name);
            TextView textViewExerciseUnits = convertView.findViewById(R.id.textView_exercise_units);

            if (spinner != null) {
                textViewSportName.setText(sport.getSportName());
                textViewExerciseUnits.setText(exercise.getExerciseUnits().toString());
            }
        }
        return convertView;
    }
}

class SpinnerArrayAdapter extends ArrayAdapter<Sport> {

    public SpinnerArrayAdapter(@NonNull Context context, List<Sport> sports) {
        super(context, 0, sports);
    }

    public void updateSports(List<Sport> sports) {
        this.clear();
        this.addAll(sports);
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    public View createItemView(int position, View convertView, ViewGroup parent) {

        Sport sport = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_sport_in_exercises, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textView_sport_name_in_exercises);

        if (sport.getSportName() != null) textViewName.setText(sport.getSportName());

        return convertView;
    }
}
