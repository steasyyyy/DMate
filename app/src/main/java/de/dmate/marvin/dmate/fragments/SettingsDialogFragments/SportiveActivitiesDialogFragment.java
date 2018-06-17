package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;

public class SportiveActivitiesDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private OnSportiveActivitiesDialogFragmentListener mListener;

    private ListView listViewSports;
    private Button buttonNewSport;
    private RelativeLayout relativeLayoutNewSport;
    private EditText editTextName;
    private EditText editTextEffect;
    private Button buttonCancelNewSport;
    private Button buttonConfirmNewSport;
    private Button buttonConfirmSports;

    private DataViewModel viewModel;

    private SportiveActivitiesArrayAdapter arrayAdapter;

    public SportiveActivitiesDialogFragment() {
        // Required empty public constructor
    }

    public static SportiveActivitiesDialogFragment newInstance() {
        SportiveActivitiesDialogFragment fragment = new SportiveActivitiesDialogFragment();
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
        return inflater.inflate(R.layout.fragment_dialog_sportive_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        listViewSports = view.findViewById(R.id.listView_sportive_activities);
        buttonNewSport = view.findViewById(R.id.button_new_sportive_activity);
        relativeLayoutNewSport = view.findViewById(R.id.relativeLayout_new_sportive_activity);
        editTextName = view.findViewById(R.id.editText_sport_name);
        editTextEffect = view.findViewById(R.id.editText_sport_effect_per_unit);
        buttonCancelNewSport = view.findViewById(R.id.button_cancel_new_sportive_activity);
        buttonConfirmNewSport = view.findViewById(R.id.button_confirm_new_sportive_activity);
        buttonConfirmSports = view.findViewById(R.id.button_confirm_sportive_activities);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        arrayAdapter = new SportiveActivitiesArrayAdapter(getContext(), new ArrayList<Sport>());

        listViewSports.setAdapter(arrayAdapter);
        listViewSports.setOnItemClickListener(this);
        registerForContextMenu(listViewSports);

        viewModel.getSports().observe(SportiveActivitiesDialogFragment.this, new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                arrayAdapter.updateSports(sports);
            }
        });

        buttonNewSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmSports.setVisibility(View.GONE);
                buttonNewSport.setVisibility(View.GONE);
                relativeLayoutNewSport.setVisibility(View.VISIBLE);
            }
        });

        buttonCancelNewSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmSports.setVisibility(View.VISIBLE);
                buttonNewSport.setVisibility(View.VISIBLE);
                relativeLayoutNewSport.setVisibility(View.GONE);

                editTextName.setText("");
                editTextEffect.setText("");
            }
        });

        buttonConfirmNewSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define a name", Toast.LENGTH_LONG);
                    toast.show();
                }

                if (editTextEffect.getText().toString().equals("") || Float.parseFloat(editTextEffect.getText().toString()) == 0f) {
                    Toast toast = Toast.makeText(getContext(), "Please define the effect on the blood sugar per unit of this sport", Toast.LENGTH_LONG);
                    toast.show();
                }

                Sport sport = new Sport();
                sport.setSportName(editTextName.getText().toString());
                sport.setSportEffectPerUnit(Integer.parseInt(editTextEffect.getText().toString()));

                viewModel.addSport(sport);

                buttonConfirmSports.setVisibility(View.VISIBLE);
                buttonNewSport.setVisibility(View.VISIBLE);
                relativeLayoutNewSport.setVisibility(View.GONE);

                editTextName.setText("");
                editTextEffect.setText("");

                Toast toast = Toast.makeText(getContext(), "New sportive activity added", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        buttonConfirmSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "Sportive activities updated", Toast.LENGTH_LONG);
                toast.show();
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSportiveActivitiesDialogFragmentListener) {
            mListener = (OnSportiveActivitiesDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBasalInsulineDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sport sport = arrayAdapter.getItem(position);

        editTextName.setText(sport.getSportName());
        editTextEffect.setText(sport.getSportEffectPerUnit().toString());

        buttonConfirmSports.setVisibility(View.GONE);
        buttonNewSport.setVisibility(View.GONE);
        relativeLayoutNewSport.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Sport sport = arrayAdapter.getItem(info.position);
                viewModel.deleteSport(sport);
                Toast toast = Toast.makeText(getContext(), "Sportive activity deleted", Toast.LENGTH_LONG);
                toast.show();
                return true;
            }
        });
    }

    public interface OnSportiveActivitiesDialogFragmentListener {

    }
}

class SportiveActivitiesArrayAdapter extends ArrayAdapter<Sport> {

    public SportiveActivitiesArrayAdapter(@NonNull Context context, List<Sport> sports) {
        super(context, 0, sports);
    }

    public void updateSports(List<Sport> sports) {
        this.clear();
        this.addAll(sports);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Sport sport = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_sport, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textView_sport_name);
        TextView textViewEffect = convertView.findViewById(R.id.textView_sport_effect);

        if (sport.getSportName() != null) textViewName.setText(sport.getSportName());
        if (sport.getSportEffectPerUnit() != null) textViewEffect.setText(sport.getSportEffectPerUnit().toString());

        return convertView;
    }
}
