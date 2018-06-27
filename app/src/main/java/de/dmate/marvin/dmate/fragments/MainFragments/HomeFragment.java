package de.dmate.marvin.dmate.fragments.MainFragments;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;
import de.dmate.marvin.dmate.util.RecyclerViewAdapterEntries;
import de.dmate.marvin.dmate.util.Helper;

public class HomeFragment extends Fragment implements RecyclerViewAdapterEntries.OnItemClickedListener, RecyclerViewAdapterEntries.OnContextMenuCreatedListener {

    //ViewModel for entries in Database
    private DataViewModel viewModel;
    private User user;

    //Custom RecyclerViewAdapterEntries to feed the RecyclerView with data (List of entries)
    private RecyclerViewAdapterEntries recyclerViewAdapterEntries;

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
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //set up OnClickListener for FAB
        //when clicked, open NewEntryActivity and set requestCode to NEW_ENTRY_REQUEST
        FloatingActionButton fab = getView().findViewById(R.id.fab_fragment_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fabNewEntry(v);
            }
        });

        //set up RecyclerView, RecyclerViewAdapterEntries and ViewModel
        recyclerView = getView().findViewById(R.id.recyclerView_fragment);
        recyclerViewAdapterEntries = new RecyclerViewAdapterEntries(new ArrayList<Entry>(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(recyclerViewAdapterEntries);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        viewModel.getUsers().observe(HomeFragment.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                try {
                    user = users.get(0);
                } catch (IndexOutOfBoundsException e) {
                    user = new User();
                    viewModel.addUser(user);
                    Toast toast = Toast.makeText(getContext(), "Created new user", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        //start observing LiveData in ViewModel and define what should happen when "LiveData<List<Entry>> entries;" in ViewModel changes
        //because it is LiveData the collection in ViewModel is always up to date (automatically gets updated when changes to database are made)
        viewModel.getEntries().observe(HomeFragment.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                recyclerViewAdapterEntries.updateItems(entries);
            }
        });

        Helper.getInstance().setRecyclerViewAdapterEntries(recyclerViewAdapterEntries);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFragmentInteractionListener");
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
        mListener.onItemClickCustomHome(v, recyclerViewAdapterEntries.getItemByPosition(position).geteId());
    }

    //create Context Menu and add "delete" option
    @Override
    public void onContextMenuCreated(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, final int position) {
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Entry entry = recyclerViewAdapterEntries.getItemByPosition(position);
                viewModel.deleteEntry(entry);
                return true;
            }
        });
    }

    //activity to host this fragment must implement these methods
    public interface OnHomeFragmentInteractionListener {
        void fabNewEntry(View v);

        void onItemClickCustomHome(View v, int entryId);
    }
}
