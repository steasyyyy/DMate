package de.dmate.marvin.dmate.fragments.BottomNavigationFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.Entry;
import de.dmate.marvin.dmate.roomDatabase.EntryViewModel;
import de.dmate.marvin.dmate.util.Helper;
import de.dmate.marvin.dmate.util.RecyclerViewAdapter;

public class HomeFragment extends Fragment implements RecyclerViewAdapter.OnItemClickedListener, RecyclerViewAdapter.OnContextMenuCreatedListener {

    //ViewModel for entries in Database
    private EntryViewModel viewModel;

    //Custom RecyclerViewAdapter to feed the RecyclerView with data (List of entries)
    private RecyclerViewAdapter recyclerViewAdapter;

    //RecyclerView = rework of ListView to show list of entries
    private RecyclerView recyclerView;

    private OnHomeFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //set up OnClickListener for FAB
        //when clicked, open NewAndUpdateEntryActivity and set requestCode to NEW_ENTRY_REQUEST
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fabNewEntry(v);
            }
        });

        //set up RecyclerView, RecyclerViewAdapter and ViewModel
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView_fragment);
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Entry>(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(recyclerViewAdapter);

        viewModel = ViewModelProviders.of(this).get(EntryViewModel.class);
        Helper.getInstance().setEntryViewModel(viewModel);

        //start observing LiveData in ViewModel and define what should happen when "LiveData<List<Entry>> entries;" in ViewModel changes
        //because it is LiveData the collection in ViewModel is always up to date (automatically gets updated when changes to database are made)
        viewModel.getEntries().observe(HomeFragment.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                recyclerViewAdapter.addItems(entries);
            }
        });

        Helper.getInstance().setRecyclerViewAdapter(recyclerViewAdapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
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

    //pass through the item clicked to MainActivity where the action is performed
    @Override
    public void onItemClick(View v, int position) {
        mListener.onItemClickCustom(v, position);
    }

    //create Context Menu and add "delete" option
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

    //activity to host this fragment must implement these methods
    public interface OnHomeFragmentInteractionListener {
        void fabNewEntry(View v);

        void onItemClickCustom(View v, int position);
    }
}
