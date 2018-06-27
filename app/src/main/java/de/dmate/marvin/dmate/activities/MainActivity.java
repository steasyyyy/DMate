package de.dmate.marvin.dmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.MainFragments.HomeFragment;
import de.dmate.marvin.dmate.fragments.MainFragments.NotificationsFragment;
import de.dmate.marvin.dmate.fragments.MainFragments.SettingsFragment;
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
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.OnHomeFragmentInteractionListener,
        NotificationsFragment.OnNotificationFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        BasalInsulinDialogFragment.OnBasalinsulinDialogFragmentInteractionListener,
        PlannedBasalInjectionsDialogFragment.OnPlannedBasalInjectionsDialogFragmentInteractionListener,
        BolusInsulinDialogFragment.OnBolusinsulinDialogFragmentInteractionListener,
        DaytimesDialogFragment.OnDaytimesDialogFragmentInteractionListener,
        ExportDialogFragment.OnExportDialogFragmentInteractionListener,
        NotificationsDialogFragment.OnNotificationsDialogFragmentInteractionListener,
        SportiveActivitiesDialogFragment.OnSportiveActivitiesDialogFragmentListener,
        TargetAreaDialogFragment.OnTargetAreaDialogFragmentInteractionListener,
        UnitsDialogFragment.OnUnitsDialogFragmentInteractionListener,
        UserNameDialogFragment.OnNameDialogFragmentInteractionListener {

    //used to determine state of NewEntryActivity (Add new entry OR edit existing entry)
    public static final int NEW_ENTRY_REQUEST = 1;
    public static final int EDIT_ENTRY_REQUEST = 2;

    //bottom navigation bar and listener
    private BottomNavigationView bottomNavView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("DMate");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_round);

        //set up BottomNavigationView and listener
        bottomNavView = findViewById(R.id.navigation);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.navigation_notifications:
                        selectedFragment = NotificationsFragment.newInstance();
                        break;
                    case R.id.navigation_settings:
                        selectedFragment = SettingsFragment.newInstance();
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        //display the default view
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, HomeFragment.newInstance());
        transaction.commit();
    }

    //passed through from HomeFragment
    @Override
    public void fabNewEntry(View v) {
        Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
        intent.putExtra("REQUEST_CODE", NEW_ENTRY_REQUEST);
        startActivity(intent);
    }

    //passed through from HomeFragment
    @Override
    public void onItemClickCustomHome(View v, int entryId) {
        Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
        intent.putExtra("REQUEST_CODE", EDIT_ENTRY_REQUEST);
        intent.putExtra("ENTRY_ID", entryId);
        startActivity(intent);
    }

    @Override
    public void onItemClickCustomNotifications(View v, int position) {
        Notification notification = (Notification) v.getTag();
        if (notification.getNotificationType().intValue() == Notification.BASAL_RATIO_ADJUST) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, SettingsFragment.newInstance());
            transaction.commit();
        }
        if (notification.getNotificationType().intValue() == Notification.BASAL_INJECTION_FORGOTTEN) {
            Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
            intent.putExtra("REQUEST_CODE", NEW_ENTRY_REQUEST);
            startActivity(intent);
        }
    }
}