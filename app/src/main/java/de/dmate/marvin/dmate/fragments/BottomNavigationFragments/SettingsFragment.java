package de.dmate.marvin.dmate.fragments.BottomNavigationFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.BasalInsulineDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.BolusInsulineDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.DaytimesDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.ExportDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.NotificationsDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.SportiveActivitiesDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.TargetAreaDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.UnitsDialogFragment;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private OnSettingsFragmentInteractionListener mListener;

    private Button daytimesButton;
    private Button bolusInsulineButton;
    private Button basalInsulineButton;
    private Button unitsButton;
    private Button targetAreaButton;
    private Button sportiveActivitiesButton;
    private Button notificationsButton;
    private Button exportButton;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get buttons used in SettingsFragment and set ClickListener to "this"
        daytimesButton = getView().findViewById(R.id.button_daytimes);
        daytimesButton.setOnClickListener(this);
        bolusInsulineButton = getView().findViewById(R.id.button_bolus_insuline);
        bolusInsulineButton.setOnClickListener(this);
        basalInsulineButton = getView().findViewById(R.id.button_basal_insuline);
        basalInsulineButton.setOnClickListener(this);
        unitsButton = getView().findViewById(R.id.button_units);
        unitsButton.setOnClickListener(this);
        targetAreaButton = getView().findViewById(R.id.button_blood_sugar_target_area);
        targetAreaButton.setOnClickListener(this);
        sportiveActivitiesButton = getView().findViewById(R.id.button_sportive_activities);
        sportiveActivitiesButton.setOnClickListener(this);
        notificationsButton = getView().findViewById(R.id.button_notifications);
        notificationsButton.setOnClickListener(this);
        exportButton = getView().findViewById(R.id.button_export);
        exportButton.setOnClickListener(this);
    }

    //manage button clicks in SettingsFragment and open dialog fragments
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_daytimes:
                new DaytimesDialogFragment().show(getFragmentManager(), "daytimesDialog");
                break;
            case R.id.button_bolus_insuline:
                new BolusInsulineDialogFragment().show(getFragmentManager(), "bolusInsulineDialog");
                break;
            case R.id.button_basal_insuline:
                new BasalInsulineDialogFragment().show(getFragmentManager(),"basalInsulineDialog");
                break;
            case R.id.button_units:
                new UnitsDialogFragment().show(getFragmentManager(), "unitsDialog");
                break;
            case R.id.button_blood_sugar_target_area:
                new TargetAreaDialogFragment().show(getFragmentManager(), "targetAreaDialog");
                break;
            case R.id.button_sportive_activities:
                new SportiveActivitiesDialogFragment().show(getFragmentManager(), "sportiveActivitiesDialog");
                break;
            case R.id.button_notifications:
                new NotificationsDialogFragment().show(getFragmentManager(), "notificationsDialog");
                break;
            case R.id.button_export:
                new ExportDialogFragment().show(getFragmentManager(), "exportDialog");
                break;
            default:
                System.out.println("Error! Could not find ID of this button.");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener) {
            mListener = (OnSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRatioWizardFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSettingsFragmentInteractionListener {
    }
}
