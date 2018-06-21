package de.dmate.marvin.dmate.fragments.PickerDialogFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.util.Calendar;

import de.dmate.marvin.dmate.R;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimePickerFragmentInteractionListener mListener;
    private Calendar calendar;

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    public static TimePickerDialogFragment newInstance(String param1, String param2) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        Bundle args = getArguments();
        calendar.setTimeInMillis(args.getLong("dateMillis"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picker_time, container, false);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mListener != null) {
            mListener.updateTime(hourOfDay, minute);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimePickerFragmentInteractionListener) {
            mListener = (OnTimePickerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimePickerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTimePickerFragmentInteractionListener {
        void updateTime(int hourOfDay, int minute);
    }
}
