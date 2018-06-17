package de.dmate.marvin.dmate.fragments.MainFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.BasalInsulinDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.BolusInsulinDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.DaytimesDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.ExportDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.PlannedBasalInjectionsDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.UserNameDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.NotificationsDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.SportiveActivitiesDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.TargetAreaDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.UnitsDialogFragment;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private OnSettingsFragmentInteractionListener mListener;

    private Button nameButton;
    private Button daytimesButton;
    private Button bolusInsulineButton;
    private Button basalInsulineButton;
    private Button plannedBasalInjectionsButton;
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
        nameButton = getView().findViewById(R.id.button_user_name);
        nameButton.setOnClickListener(this);
        daytimesButton = getView().findViewById(R.id.button_daytimes);
        daytimesButton.setOnClickListener(this);
        bolusInsulineButton = getView().findViewById(R.id.button_bolus_insulin);
        bolusInsulineButton.setOnClickListener(this);
        basalInsulineButton = getView().findViewById(R.id.button_basal_insulin);
        basalInsulineButton.setOnClickListener(this);
        plannedBasalInjectionsButton = getView().findViewById(R.id.button_planned_basal_injections);
        plannedBasalInjectionsButton.setOnClickListener(this);
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
            case R.id.button_user_name:
                new UserNameDialogFragment().show(getFragmentManager(), "nameDialog");
                break;
            case R.id.button_daytimes:
                new DaytimesDialogFragment().show(getFragmentManager(), "daytimesDialog");
                break;
            case R.id.button_bolus_insulin:
                new BolusInsulinDialogFragment().show(getFragmentManager(), "bolusInsulineDialog");
                break;
            case R.id.button_basal_insulin:
                BasalInsulinDialogFragment fragment = new BasalInsulinDialogFragment();
                fragment.show(getFragmentManager(),"basalInsulineDialog");
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
            case R.id.button_planned_basal_injections:
                new PlannedBasalInjectionsDialogFragment().show(getFragmentManager(), "plannedBasalInjectionsDialog");
            default:
                System.out.println("Error! Could not find ID of this button.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_settings, container, false);
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
