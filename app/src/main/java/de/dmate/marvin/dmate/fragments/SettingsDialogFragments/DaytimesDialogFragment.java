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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;

public class DaytimesDialogFragment extends DialogFragment {

    private OnDaytimesDialogFragmentInteractionListener mListener;
    private Dialog dialog;

    private ListView listViewDaytimes;
    private Button buttonNewDaytime;
    private RelativeLayout newDaytimeLayout;
    private EditText editTextDaytimeStartHH;
    private EditText editTextDaytimeStartMM;
    private EditText editTextDaytimeEndHH;
    private EditText editTextDaytimeEndMM;
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
        buttoncancelNewDaytime = view.findViewById(R.id.button_cancel_new_daytime);
        buttonConfirmNewDaytime = view.findViewById(R.id.button_confirm_new_daytime);
        buttonConfirmDaytimes = view.findViewById(R.id.button_confirm_daytimes);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        //create DaytimeArrayAdapter with empty data collection
        arrayAdapter = new DaytimeArrayAdapter(getContext(), new ArrayList<Daytime>());

        //set adapter in listview
        listViewDaytimes.setAdapter(arrayAdapter);

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
            }
        });

        buttonConfirmNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save new daytime
                buttonConfirmDaytimes.setVisibility(View.VISIBLE);
                newDaytimeLayout.setVisibility(View.GONE);
                buttonNewDaytime.setVisibility(View.VISIBLE);
                String daytimeStartString = editTextDaytimeStartHH.getText().toString() + ":" + editTextDaytimeStartMM.getText().toString();
                String daytimeEndString = editTextDaytimeEndHH.getText().toString() + ":" + editTextDaytimeEndMM.getText().toString();

                Daytime daytime = new Daytime();
                daytime.setDaytimeStart(daytimeStartString);
                daytime.setDaytimeEnd(daytimeEndString);
                //TODO set buFactor, correctionFactor and buFactorConsultingArithMean

                viewModel.addDaytime(daytime);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.daytime_layout_new, parent, false);
        }

        TextView textViewStart = convertView.findViewById(R.id.textView_daytime_start);
        TextView textViewEnd = convertView.findViewById(R.id.textView_daytime_end);

        textViewStart.setText(currentDaytime.getDaytimeStart());
        textViewEnd.setText(currentDaytime.getDaytimeEnd());

        return convertView;
    }

}
