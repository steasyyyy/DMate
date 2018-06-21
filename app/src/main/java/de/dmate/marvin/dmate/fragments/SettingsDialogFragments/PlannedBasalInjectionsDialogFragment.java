package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;

public class PlannedBasalInjectionsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private OnPlannedBasalInjectionsDialogFragmentInteractionListener mListener;

    private DataViewModel viewModel;

    PlannedBasalInjectionArrayAdapter arrayAdapter;

    private ListView listView;
    private Button buttonNewPlannedBasalInjection;
    private Button buttonConfirmPlannedBasalInjections;
    private Button buttonCancelNewPlannedBasalInjection;
    private Button buttonConfirmNewPlannedBasalInjection;
    private RelativeLayout newPlannedBasalInjectionLayout;
    private EditText editTextHours;
    private EditText editTextMinutes;
    private EditText editTextAmount;

    public PlannedBasalInjectionsDialogFragment() {
        // Required empty public constructor
    }

    public static PlannedBasalInjectionsDialogFragment newInstance() {
        PlannedBasalInjectionsDialogFragment fragment = new PlannedBasalInjectionsDialogFragment();
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
        return inflater.inflate(R.layout.fragment_settings_dialog_planned_basal_injections, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        listView = view.findViewById(R.id.listView_plannedBasalInjections);
        buttonNewPlannedBasalInjection = view.findViewById(R.id.button_new_planned_injection);
        buttonConfirmPlannedBasalInjections = view.findViewById(R.id.button_confirm_planned_basal_injections);
        buttonCancelNewPlannedBasalInjection = view.findViewById(R.id.button_cancel_new_planned_basal);
        buttonConfirmNewPlannedBasalInjection = view.findViewById(R.id.button_confirm_new_planned_basal);
        newPlannedBasalInjectionLayout = view.findViewById(R.id.relativeLayout_new_planned_basal);
        editTextHours = view.findViewById(R.id.editText_planned_hours);
        editTextMinutes = view.findViewById(R.id.editText_planned_minutes);
        editTextAmount = view.findViewById(R.id.editText_planned_amount);

        arrayAdapter = new PlannedBasalInjectionArrayAdapter(getContext(), new ArrayList<PlannedBasalInjection>());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        viewModel.getPlannedBasalInjections().observe(PlannedBasalInjectionsDialogFragment.this, new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {
                arrayAdapter.updatePlannedBasalInjections(plannedBasalInjections);
            }
        });

        buttonNewPlannedBasalInjection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmPlannedBasalInjections.setVisibility(View.GONE);
                buttonNewPlannedBasalInjection.setVisibility(View.GONE);
                newPlannedBasalInjectionLayout.setVisibility(View.VISIBLE);
            }
        });

        buttonCancelNewPlannedBasalInjection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmPlannedBasalInjections.setVisibility(View.VISIBLE);
                buttonNewPlannedBasalInjection.setVisibility(View.VISIBLE);
                newPlannedBasalInjectionLayout.setVisibility(View.GONE);

                editTextHours.setText("");
                editTextMinutes.setText("");
                editTextAmount.setText("");
            }
        });

        buttonConfirmNewPlannedBasalInjection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextHours.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define hours", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (editTextAmount.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define the amount of basal insulin", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (editTextHours.getText().toString().length() == 1) {
                    editTextHours.setText("0" + editTextHours.getText().toString());
                }

                if (editTextMinutes.getText().toString().equals("")) {
                    editTextMinutes.setText("00");
                }

                if (editTextMinutes.getText().toString().length() == 1) {
                    editTextMinutes.setText("0" + editTextMinutes.getText().toString());
                }

                if (Integer.parseInt(editTextHours.getText().toString()) < 0 || Integer.parseInt(editTextHours.getText().toString()) > 23) {
                    Toast toast = Toast.makeText(getContext(), "Hours must be between 0 and 23", Toast.LENGTH_LONG);
                    toast.show();
                }

                if (Integer.parseInt(editTextMinutes.getText().toString()) < 0 || Integer.parseInt(editTextMinutes.getText().toString()) > 23) {
                    Toast toast = Toast.makeText(getContext(), "Minutes must be between 0 and 59", Toast.LENGTH_LONG);
                    toast.show();
                }

                String timeOfDayString = editTextHours.getText().toString() + ":" + editTextMinutes.getText().toString();
                Float amount = Float.parseFloat(editTextAmount.getText().toString());

                PlannedBasalInjection plannedBasalInjection = new PlannedBasalInjection();
                plannedBasalInjection.setTimeOfDay(timeOfDayString);
                plannedBasalInjection.setBasal(amount);

                viewModel.addPlannedBasalInjection(plannedBasalInjection);

                buttonConfirmPlannedBasalInjections.setVisibility(View.VISIBLE);
                buttonNewPlannedBasalInjection.setVisibility(View.VISIBLE);
                newPlannedBasalInjectionLayout.setVisibility(View.GONE);

                editTextHours.setText("");
                editTextMinutes.setText("");
                editTextAmount.setText("");

                Toast toast = Toast.makeText(getContext(), "Planned basal insulin injection added", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        buttonConfirmPlannedBasalInjections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "Updated planned basal insulin injections", Toast.LENGTH_LONG);
                toast.show();
                dismiss();
            }
        });



        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlannedBasalInjectionsDialogFragmentInteractionListener) {
            mListener = (OnPlannedBasalInjectionsDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlannedBasalInjectionsDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlannedBasalInjection plannedBasalInjection = arrayAdapter.getItem(position);

        String hours = plannedBasalInjection.getTimeOfDay().charAt(0) + "" + plannedBasalInjection.getTimeOfDay().charAt(1);
        String minutes = plannedBasalInjection.getTimeOfDay().charAt(3) + "" + plannedBasalInjection.getTimeOfDay().charAt(4);
        Float amount = plannedBasalInjection.getBasal();

        editTextHours.setText(hours);
        editTextMinutes.setText(minutes);
        editTextAmount.setText(amount.toString());

        buttonConfirmPlannedBasalInjections.setVisibility(View.GONE);
        buttonNewPlannedBasalInjection.setVisibility(View.GONE);
        newPlannedBasalInjectionLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PlannedBasalInjection plannedBasalInjection = arrayAdapter.getItem(info.position);
                viewModel.deletePlannedBasalInjection(plannedBasalInjection);
                Toast toast = Toast.makeText(getContext(), "Planned basal injection deleted", Toast.LENGTH_LONG);
                toast.show();
                return true;
            }
        });
    }

    public interface OnPlannedBasalInjectionsDialogFragmentInteractionListener {

    }
}

class PlannedBasalInjectionArrayAdapter extends ArrayAdapter<PlannedBasalInjection> {

    public PlannedBasalInjectionArrayAdapter(Context context, List<PlannedBasalInjection> plannedBasalInjections) {
        super(context, 0, plannedBasalInjections);
    }

    public void updatePlannedBasalInjections(List<PlannedBasalInjection> plannedBasalInjections) {
        this.clear();
        this.addAll(plannedBasalInjections);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PlannedBasalInjection plannedBasalInjection = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_planned_basal_injection, parent, false);
        }

        TextView plannedBasalTime = convertView.findViewById(R.id.textView_planned_basal_time);
        TextView plannedBasalAmount = convertView.findViewById(R.id.textView_planned_basal_amount);

        plannedBasalTime.setText(plannedBasalInjection.getTimeOfDay());
        plannedBasalAmount.setText(plannedBasalInjection.getBasal().toString());

        return convertView;
    }
}
