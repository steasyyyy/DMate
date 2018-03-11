package de.dmate.marvin.dmate.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;

import de.dmate.marvin.dmate.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDatePickerFragmentInteractionListener mListener;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    public static DatePickerFragment newInstance(String param1, String param2) {
        DatePickerFragment fragment = new DatePickerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickerFragmentInteractionListener) {
            mListener = (OnDatePickerFragmentInteractionListener) context;
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (mListener != null) {
            mListener.updateDate(year, month, dayOfMonth);
        }
    }

    public interface OnDatePickerFragmentInteractionListener {
        void updateDate(int year, int month, int dayOfMonth);
    }
}
