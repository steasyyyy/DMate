package de.dmate.marvin.dmate.fragments.OtherFragments;

import android.app.Dialog;
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

import java.util.List;

import de.dmate.marvin.dmate.R;
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

    private ExercisesArrayAdapter exercisesAdapter;
    private SpinnerArrayAdapter spinnerAdapter;

    private List<Sport> sports;
    private List<Exercise> exercisesOfEntry;

    public ExercisesDialogFragment() {

    }

    public static ExercisesDialogFragment newInstance() {
        return new ExercisesDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setView(new View(getActivity())).create();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
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

        exercisesAdapter = new ExercisesArrayAdapter(getContext(), exercisesOfEntry, sports);
        listViewExercises.setAdapter(exercisesAdapter);

        spinnerAdapter = new SpinnerArrayAdapter(getContext(), sports);
        spinnerSelectSport.setAdapter(spinnerAdapter);

        listViewExercises.setOnItemClickListener(this);
        registerForContextMenu(listViewExercises);

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
                    Toast toast = Toast.makeText(getContext(), "Please select a sportive activity or create a new one in the settings first", Toast.LENGTH_LONG);
                    toast.show();
                }
                if (editTextUnits.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define the number of units you are going to exercise", Toast.LENGTH_LONG);
                    toast.show();
                }
                Exercise newExercise = new Exercise();
                newExercise.setsIdF(((Sport)spinnerSelectSport.getSelectedItem()).getsId());
                newExercise.setExerciseUnits(Float.parseFloat(editTextUnits.getText().toString()));

                boolean alreadyExisting = false;
                for (Exercise exOfEntry : exercisesOfEntry) {
                    if (exOfEntry.getsIdF().equals(newExercise.getsIdF()) && exOfEntry.getExerciseUnits().equals(newExercise.getExerciseUnits())) {
                        alreadyExisting = true;
                        Toast toast = Toast.makeText(getContext(), "Duplicate exercises for the same entry are not allowed", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                if (!alreadyExisting) {
                    exercisesOfEntry.add(newExercise);
//                    exercisesAdapter.updateExercises(exercisesOfEntry);
                }
                buttonConfirmExercises.setVisibility(View.VISIBLE);
                buttonNewExercise.setVisibility(View.VISIBLE);
                relativeLayoutNewExercise.setVisibility(View.GONE);

                spinnerSelectSport.setSelection(0);
                editTextUnits.setText("");
            }
        });

        buttonConfirmExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                exercisesOfEntry.remove(exercise);
                exercisesAdapter.updateExercises(exercisesOfEntry);
                return true;
            }
        });
    }

    public void setExercisesOfEntry(List<Exercise> exercisesOfEntry) {
        this.exercisesOfEntry = exercisesOfEntry;
    }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mListener.setExercises(exercisesOfEntry);
        super.onDismiss(dialog);
    }

    public interface OnExercisesDialogFragmentListener {
        void setExercises(List<Exercise> exercisesOfEntry);
    }
}

class ExercisesArrayAdapter extends ArrayAdapter<Exercise> {
    private List<Sport> sports;

    public ExercisesArrayAdapter(@NonNull Context context, List<Exercise> exercises, List<Sport> sports) {
        super(context, 0, exercises);
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

            textViewSportName.setText(sport.getSportName());
            textViewExerciseUnits.setText(exercise.getExerciseUnits().toString());

        }
        return convertView;
    }
}

class SpinnerArrayAdapter extends ArrayAdapter<Sport> {

    public SpinnerArrayAdapter(@NonNull Context context, List<Sport> sports) {
        super(context, 0, sports);
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
