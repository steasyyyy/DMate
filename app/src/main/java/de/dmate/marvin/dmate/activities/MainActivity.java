package de.dmate.marvin.dmate.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.Entry;
import de.dmate.marvin.dmate.roomDatabase.EntryViewModel;
import de.dmate.marvin.dmate.util.RecyclerViewAdapter;
import de.dmate.marvin.dmate.util.DMateApplication;
import de.dmate.marvin.dmate.util.Helper;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickedListener, RecyclerViewAdapter.OnContextMenuCreatedListener {

    public static final int NEW_ENTRY_REQUEST = 1;
    public static final int EDIT_ENTRY_REQUEST = 2;

    //ViewModel for entries in Database
    private EntryViewModel viewModel;

    //Custom RecyclerViewAdapter to feed the RecyclerView with data (List of entries)
    private RecyclerViewAdapter recyclerViewAdapter;

    //RecyclerView = rework of ListView to show list of entries
    private RecyclerView recyclerView;

    //bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_ratio_wizard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
                case R.id.navigation_settings:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        //set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("DMate");
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_material_light));
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_round);

        //set up OnClickListener for FAB
        //when clicked, open NewAndUpdateEntryActivity and set requestCode to NEW_ENTRY_REQUEST
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewAndUpdateEntryActivity.class);
                intent.putExtra("REQUEST_CODE", NEW_ENTRY_REQUEST);
                startActivity(intent);
            }
        });

        //set application context in DMateApplication (custom application object)
        DMateApplication app = (DMateApplication) getApplication();
        app.initialize(getApplicationContext());

        //pass Application Object to Helper
        Helper.getInstance().setApplication(app);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Entry>(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);

//        registerForContextMenu(recyclerView);

        viewModel = ViewModelProviders.of(this).get(EntryViewModel.class);
        Helper.getInstance().setEntryViewModel(viewModel);

        //start observing LiveData in ViewModel and define what should happen when "LiveData<List<Entry>> entries;" in ViewModel changes
        //because it is LiveData the collection in ViewModel is always up to date (automatically gets updated when changes to database are made)
        viewModel.getEntries().observe(MainActivity.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                recyclerViewAdapter.addItems(entries);
            }
        });

        Helper.getInstance().setRecyclerViewAdapter(recyclerViewAdapter);
    }

//    @Override
//    //set menu (containing the actions) for the app bar
//    //set functionality to buttons in method: onOptionsItemSelected(MenuItem item)
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.app_bar_actions, menu);
//        menu.findItem(R.id.action_save).setVisible(false);
//        menu.findItem(R.id.action_delete_forever).setVisible(false);
//        menu.findItem(R.id.action_refresh).setVisible(false);
//        menu.findItem(R.id.action_settings).setVisible(false);
//        return super.onCreateOptionsMenu(menu);
//    }

//    //set functionality to buttons selected in the app bar menu
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            //TODO
//            //this is bs for now, if needed -> refactor
//            case R.id.action_refresh :
////                this.onResume();
//            case R.id.action_delete_forever:
////                ((DMateApplication)getApplication()).resetAllPrefs();
////                this.recreate();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    //set action when item is clicked -> start NewAndUpdateEntryActivity as EDIT_ENTRY_REQUEST
    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(MainActivity.this, NewAndUpdateEntryActivity.class);
        intent.putExtra("REQUEST_CODE", EDIT_ENTRY_REQUEST);
        intent.putExtra("POSITION", position);

        startActivity(intent);
    }

    //set up Context menu and action for selected items
    @Override
    public void onContextMenuCreated(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, final int position) {
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Entry entry = recyclerViewAdapter.getItemByPosition(position);
                viewModel.deleteEntry(entry);
                return false;
            }
        });
    }
}