package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.edmodo.rangebar.RangeBar;

import java.util.ArrayList;

import de.dmate.marvin.dmate.R;

public class DaytimesDialogFragment extends DialogFragment implements RangeBar.OnRangeBarChangeListener {

    private OnDaytimesDialogFragmentInteractionListener mListener;

    private RangeBar rangeBar;
    private ListView listView;

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

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_daytimes, container, false);

        rangeBar = view.findViewById(R.id.daytimes_rangeBar);
        rangeBar.setOnRangeBarChangeListener(this);
        rangeBar.setBarColor(getResources().getColor(R.color.colorAccent));
        rangeBar.setConnectingLineColor(getResources().getColor(R.color.colorAccent));
        rangeBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.colorAccent));
        rangeBar.setThumbColorNormal(getResources().getColor(R.color.colorAccent));
        rangeBar.setThumbColorPressed(getResources().getColor(R.color.colorAccent));
        rangeBar.setTickCount(24);

        listView = view.findViewById(R.id.listView_daytimes);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Daytime1");
        arrayList.add("Daytime2");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.daytime_layout, arrayList);
        listView.setAdapter(arrayAdapter);


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

    //interface implementation for RangeBar
    @Override
    public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {

    }


    public interface OnDaytimesDialogFragmentInteractionListener {

    }
}
