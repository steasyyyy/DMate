package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.Duration;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;

public class DaytimesDialogFragment extends DialogFragment implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {

    private OnDaytimesDialogFragmentInteractionListener mListener;
    private Dialog dialog;

    private ListView listViewDaytimes;
    private Button buttonNewDaytime;
    private RelativeLayout newDaytimeLayout;
    private EditText editTextDaytimeStartHH;
    private EditText editTextDaytimeStartMM;
    private EditText editTextDaytimeEndHH;
    private EditText editTextDaytimeEndMM;
    private EditText editTextCorrectionFactor;
    private EditText editTextBuFactor;
    private Button buttoncancelNewDaytime;
    private Button buttonConfirmNewDaytime;
    private Button buttonConfirmDaytimes;

    private DaytimeArrayAdapter arrayAdapter;
    private DataViewModel viewModel;

    public DaytimesDialogFragment() {
        // Required empty public constructor
    }

    public static DaytimesDialogFragment newInstance() {
        DaytimesDialogFragment fragment = new DaytimesDialogFragment();
        return fragment;
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

        this.dialog = dialog;

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_daytimes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //initialize all views
        listViewDaytimes = view.findViewById(R.id.listView_daytimes);
        buttonNewDaytime = view.findViewById(R.id.button_new_daytime);
        newDaytimeLayout = view.findViewById(R.id.new_daytime_layout);
        editTextDaytimeStartHH = view.findViewById(R.id.editText_daytimeStart_HH);
        editTextDaytimeStartMM = view.findViewById(R.id.editText_daytimeStart_MM);
        editTextDaytimeEndHH = view.findViewById(R.id.editText_daytimeEnd_HH);
        editTextDaytimeEndMM = view.findViewById(R.id.editText_daytimeEnd_MM);
        editTextCorrectionFactor = view.findViewById(R.id.editText_correctionFactor);
        editTextBuFactor = view.findViewById(R.id.editText_buFactor);
        buttoncancelNewDaytime = view.findViewById(R.id.button_cancel_new_daytime);
        buttonConfirmNewDaytime = view.findViewById(R.id.button_confirm_new_daytime);
        buttonConfirmDaytimes = view.findViewById(R.id.button_confirm_daytimes);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        //create DaytimeArrayAdapter with empty data collection
        arrayAdapter = new DaytimeArrayAdapter(getContext(), new ArrayList<Daytime>());

        //set adapter and clicklistener for listview
        listViewDaytimes.setAdapter(arrayAdapter);
        listViewDaytimes.setOnItemClickListener(this);
        registerForContextMenu(listViewDaytimes);

        //observe LiveData from DB to trigger update of adapter content
        viewModel.getDaytimes().observe(DaytimesDialogFragment.this, new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                arrayAdapter.updateDaytimes(daytimes);
            }
        });

        //set click listeners to all buttons
        buttonNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmDaytimes.setVisibility(View.GONE);
                buttonNewDaytime.setVisibility(View.GONE);
                newDaytimeLayout.setVisibility(View.VISIBLE);
            }
        });

        buttoncancelNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmDaytimes.setVisibility(View.VISIBLE);
                newDaytimeLayout.setVisibility(View.GONE);
                buttonNewDaytime.setVisibility(View.VISIBLE);
                //clear all editTexts
                editTextDaytimeStartHH.setText("");
                editTextDaytimeStartMM.setText("");
                editTextDaytimeEndHH.setText("");
                editTextDaytimeEndMM.setText("");
                editTextCorrectionFactor.setText("");
                editTextBuFactor.setText("");
            }
        });

        buttonConfirmNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if hours are = "" and trigger Toast if so
                if (editTextDaytimeStartHH.getText().toString().equals("") || editTextDaytimeEndHH.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define daytime hours", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                //check if minutes are = "" and set to "00" if so
                if (editTextDaytimeStartMM.getText().toString().equals("") || editTextDaytimeStartMM.getText().toString().equals("0")) editTextDaytimeStartMM.setText("00");
                if (editTextDaytimeEndMM.getText().toString().equals("") || editTextDaytimeStartMM.getText().toString().equals("0")) editTextDaytimeEndMM.setText("00");

                //check if correction factor is = "" and trigger Toast if so
                if (editTextCorrectionFactor.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define a correction factor", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                //check if BU factor is = "" and trigger Toast if so
                if (editTextBuFactor.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Please define a bread unit factor", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                String daytimeStartString = editTextDaytimeStartHH.getText().toString() + ":" + editTextDaytimeStartMM.getText().toString();
                String daytimeEndString = editTextDaytimeEndHH.getText().toString() + ":" + editTextDaytimeEndMM.getText().toString();
                Integer correctionFactor = Integer.parseInt(editTextCorrectionFactor.getText().toString());
                Float buFactor = Float.parseFloat(editTextBuFactor.getText().toString());

                //validate hours of daytimes -> must be between 0 and 23
                if (!(Integer.parseInt(editTextDaytimeStartHH.getText().toString()) <= 23
                        && Integer.parseInt(editTextDaytimeStartHH.getText().toString()) >= 0
                        && Integer.parseInt(editTextDaytimeEndHH.getText().toString()) <= 23
                        && Integer.parseInt(editTextDaytimeEndHH.getText().toString()) >= 0)) {
                    Toast toast = Toast.makeText(getContext(), "Daytime hours must be between 0 and 23", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                //validate minutes of daytimes -> must be between 0 and 59
                if (!(Integer.parseInt(editTextDaytimeStartMM.getText().toString()) <= 59
                        && Integer.parseInt(editTextDaytimeStartMM.getText().toString()) >= 0
                        && Integer.parseInt(editTextDaytimeEndMM.getText().toString()) <= 59
                        && Integer.parseInt(editTextDaytimeEndMM.getText().toString()) >= 0)) {
                    Toast toast = Toast.makeText(getContext(), "Daytime minutes must be between 0 and 59", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                Daytime daytime = new Daytime();
                daytime.setDaytimeStart(daytimeStartString);
                daytime.setDaytimeEnd(daytimeEndString);
                daytime.setCorrectionFactor(correctionFactor);
                daytime.setBuFactor(buFactor);

                viewModel.addDaytime(daytime);

                buttonConfirmDaytimes.setVisibility(View.VISIBLE);
                newDaytimeLayout.setVisibility(View.GONE);
                buttonNewDaytime.setVisibility(View.VISIBLE);

                //clear all editTexts
                editTextDaytimeStartHH.setText("");
                editTextDaytimeStartMM.setText("");
                editTextDaytimeEndHH.setText("");
                editTextDaytimeEndMM.setText("");
                editTextCorrectionFactor.setText("");
                editTextBuFactor.setText("");
            }
        });

        buttonConfirmDaytimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save daytimes if any changed
                buttonConfirmDaytimes.setVisibility(View.VISIBLE);
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDaytimesDialogFragmentInteractionListener) {
            mListener = (OnDaytimesDialogFragmentInteractionListener) context;
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
    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Daytime currentDaytime = arrayAdapter.getItem(position);

        String daytimeStartHH = currentDaytime.getDaytimeStart().charAt(0) + "" + currentDaytime.getDaytimeStart().charAt(1);
        String daytimeStartMM = currentDaytime.getDaytimeStart().charAt(3) + "" + currentDaytime.getDaytimeStart().charAt(4);
        String daytimeEndHH = currentDaytime.getDaytimeEnd().charAt(0) + "" + currentDaytime.getDaytimeEnd().charAt(1);
        String daytimeEndMM = currentDaytime.getDaytimeEnd().charAt(3) + "" + currentDaytime.getDaytimeEnd().charAt(4);

        Integer correctionFactor = currentDaytime.getCorrectionFactor();
        Float buFactor = currentDaytime.getBuFactor();

        editTextDaytimeStartHH.setText(daytimeStartHH);
        editTextDaytimeStartMM.setText(daytimeStartMM);
        editTextDaytimeEndHH.setText(daytimeEndHH);
        editTextDaytimeEndMM.setText(daytimeEndMM);
        editTextCorrectionFactor.setText(correctionFactor.toString());
        editTextBuFactor.setText(buFactor.toString());

        buttonConfirmDaytimes.setVisibility(View.GONE);
        buttonNewDaytime.setVisibility(View.GONE);
        newDaytimeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Daytime daytime = arrayAdapter.getItem(info.position);
                viewModel.deleteDaytime(daytime);
                return true;
            }
        });
    }

    public interface OnDaytimesDialogFragmentInteractionListener {

    }
}

//Custom ArrayAdapter to feed ListView with content from DB
class DaytimeArrayAdapter extends ArrayAdapter<Daytime> {

    public DaytimeArrayAdapter(Context context, List<Daytime> daytimes) {
        super(context, 0, daytimes);
    }

    public void updateDaytimes(List<Daytime> daytimes) {
        this.clear();
        this.addAll(daytimes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Daytime currentDaytime = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.daytime_layout, parent, false);
        }

        TextView textViewStart = convertView.findViewById(R.id.textView_daytime_start);
        TextView textViewEnd = convertView.findViewById(R.id.textView_daytime_end);
        TextView textViewCorrectionFactor = convertView.findViewById(R.id.textView_correctionFactor_new);
        TextView textViewBuFactor = convertView.findViewById(R.id.textView_buFactor_new);

        textViewStart.setText(currentDaytime.getDaytimeStart());
        textViewEnd.setText(currentDaytime.getDaytimeEnd());
        textViewCorrectionFactor.setText(currentDaytime.getCorrectionFactor().toString());
        textViewBuFactor.setText(currentDaytime.getBuFactor().toString());

        return convertView;
    }
}
