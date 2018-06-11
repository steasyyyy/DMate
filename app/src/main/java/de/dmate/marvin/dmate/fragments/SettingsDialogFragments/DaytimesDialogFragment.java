package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.LayoutInflaterCompat;
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
        View view = inflater.inflate(R.layout.fragment_dialog_daytimes, container, false);

        //initialize all buttons etc.
        buttonNewDaytime = view.findViewById(R.id.button_new_daytime);
        newDaytimeLayout = view.findViewById(R.id.new_daytime_layout);
        buttoncancelNewDaytime = view.findViewById(R.id.button_cancel_new_daytime);
        buttonConfirmNewDaytime = view.findViewById(R.id.button_confirm_new_daytime);
        buttonConfirmDaytimes = view.findViewById(R.id.button_confirm_daytimes);
        editTextDaytimeStartHH = view.findViewById(R.id.editText_daytimeStart_HH);
        editTextDaytimeStartMM = view.findViewById(R.id.editText_daytimeStart_MM);
        editTextDaytimeEndHH = view.findViewById(R.id.editText_daytimeEnd_HH);
        editTextDaytimeEndMM = view.findViewById(R.id.editText_daytimeEnd_MM);

        arrayAdapter = new DaytimeArrayAdapter(getContext(), new ArrayList<Daytime>());

        //initialize listview
        listViewDaytimes = view.findViewById(R.id.listView_daytimes);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        viewModel.getDaytimes().observe(DaytimesDialogFragment.this, new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                arrayAdapter.updateDaytimes(daytimes);
            }
        });

//        ArrayList<String> arrayList = new ArrayList<String>();
//        arrayList.add("Daytime1");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");
//        arrayList.add("Daytime2");

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.daytime_layout, arrayList);
        listViewDaytimes.setAdapter(arrayAdapter);



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

        return view;
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

class DaytimeArrayAdapter extends ArrayAdapter<Daytime> {

    private Context context;
    private List<Daytime> daytimes;

    public DaytimeArrayAdapter(Context context, ArrayList<Daytime> daytimes) {
        super(context, 0, daytimes);

        this.context = context;
        this.daytimes = daytimes;
    }

    public void updateDaytimes(List<Daytime> daytimes) {
        this.addAll(daytimes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.daytime_layout_new, parent, false);
        }

        View daytimeView = convertView;

        Daytime currentDaytime = daytimes.get(position);

        TextView textViewStart = daytimeView.findViewById(R.id.textView_daytime_start);
        TextView textViewEnd = daytimeView.findViewById(R.id.textView_daytime_end);

        textViewStart.setText(currentDaytime.getDaytimeStart());
        textViewEnd.setText(currentDaytime.getDaytimeEnd());

        return daytimeView;
    }

}
