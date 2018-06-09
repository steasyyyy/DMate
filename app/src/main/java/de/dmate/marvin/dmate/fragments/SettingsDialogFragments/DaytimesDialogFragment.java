package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.edmodo.rangebar.RangeBar;

import java.util.ArrayList;

import de.dmate.marvin.dmate.R;

public class DaytimesDialogFragment extends DialogFragment {

    private OnDaytimesDialogFragmentInteractionListener mListener;
    private Dialog dialog;

    private ListView listViewDaytimes;
    private Button buttonNewDaytime;
    private RelativeLayout newDaytimeLayout;
    private EditText editTextDaytimeStart;
    private EditText editTextDaytimeEnd;
    private Button buttoncancelNewDaytime;
    private Button buttonConfirmNewDaytime;
    private Button buttonConfirmDaytimes;


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

        //initialize listview
        listViewDaytimes = view.findViewById(R.id.listView_daytimes);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Daytime1");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        arrayList.add("Daytime2");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.daytime_layout, arrayList);
        listViewDaytimes.setAdapter(arrayAdapter);

        //initialize all buttons etc.
        buttonNewDaytime = view.findViewById(R.id.button_new_daytime);
        newDaytimeLayout = view.findViewById(R.id.new_daytime_layout);
        buttoncancelNewDaytime = view.findViewById(R.id.button_cancel_new_daytime);
        buttonConfirmNewDaytime = view.findViewById(R.id.button_confirm_new_daytime);
        buttonConfirmDaytimes = view.findViewById(R.id.button_confirm_daytimes);
        editTextDaytimeStart = view.findViewById(R.id.editText_daytimeStart);
        editTextDaytimeEnd = view.findViewById(R.id.editText_daytimeEnd);

        buttonNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmDaytimes.setVisibility(View.GONE);
                newDaytimeLayout.setVisibility(View.VISIBLE);
            }
        });

        buttoncancelNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirmDaytimes.setVisibility(View.VISIBLE);
                newDaytimeLayout.setVisibility(View.GONE);
            }
        });

        buttonConfirmNewDaytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save new daytime
                buttonConfirmDaytimes.setVisibility(View.VISIBLE);
                newDaytimeLayout.setVisibility(View.GONE);
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
