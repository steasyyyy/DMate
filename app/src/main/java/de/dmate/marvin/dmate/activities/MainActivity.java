package de.dmate.marvin.dmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.fragments.BottomNavigationFragments.HomeFragment;
import de.dmate.marvin.dmate.fragments.BottomNavigationFragments.NotificationsFragment;
import de.dmate.marvin.dmate.fragments.BottomNavigationFragments.SettingsFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.BasalInsulineDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.BolusInsulineDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.DaytimesDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.ExportDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.NameDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.NotificationsDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.SportiveActivitiesDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.TargetAreaDialogFragment;
import de.dmate.marvin.dmate.fragments.SettingsDialogFragments.UnitsDialogFragment;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.util.DMateApplication;
import de.dmate.marvin.dmate.util.EntriesRecyclerViewAdapter;
import de.dmate.marvin.dmate.util.Helper;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.OnHomeFragmentInteractionListener,
        NotificationsFragment.OnNotificationFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        BasalInsulineDialogFragment.OnBasalInsulineDialogFragmentInteractionListener,
        BolusInsulineDialogFragment.OnBolusInsulineDialogFragmentInteractionListener,
        DaytimesDialogFragment.OnDaytimesDialogFragmentInteractionListener,
        ExportDialogFragment.OnExportDialogFragmentInteractionListener,
        NotificationsDialogFragment.OnNotificationsDialogFragmentInteractionListener,
        SportiveActivitiesDialogFragment.OnSportiveActivitiesDialogFragmentListener,
        TargetAreaDialogFragment.OnTargetAreaDialogFragmentInteractionListener,
        UnitsDialogFragment.OnUnitsDialogFragmentInteractionListener,
        NameDialogFragment.OnNameDialogFragmentInteractionListener {

    //used to determine state of NewEntryActivity (Add new entry OR edit existing entry)
    public static final int NEW_ENTRY_REQUEST = 1;
    public static final int EDIT_ENTRY_REQUEST = 2;

    //ViewModel for entries in Database
    private DataViewModel viewModel;

    //Custom EntriesRecyclerViewAdapter to feed the RecyclerView with data (List of entries)
    private EntriesRecyclerViewAdapter entriesRecyclerViewAdapter;

    //RecyclerView = rework of ListView to show list of entries
    private RecyclerView recyclerView;

    //bottom navigation bar and listener
    private BottomNavigationView bottomNavView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("DMate");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_round);

        //set application context in DMateApplication (custom application object)
        DMateApplication app = (DMateApplication) getApplication();
        app.initialize(getApplicationContext());

        //pass Application Object to Helper
        Helper.getInstance().setApplication(app);

        //set up BottomNavigationView and listener
        bottomNavView = (BottomNavigationView) findViewById(R.id.navigation);
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

    //from HomeFragment
    @Override
    public void fabNewEntry(View v) {
        Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
        intent.putExtra("REQUEST_CODE", NEW_ENTRY_REQUEST);
        startActivity(intent);
    }

    //from HomeFragment
    @Override
    public void onItemClickCustom(View v, int position) {
        Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
        intent.putExtra("REQUEST_CODE", EDIT_ENTRY_REQUEST);
        intent.putExtra("POSITION", position);
        startActivity(intent);
    }
}