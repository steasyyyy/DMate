package de.dmate.marvin.dmate.fragments.MainFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;
import de.dmate.marvin.dmate.util.recyclerViewAdapter.RecyclerViewAdapterNotifications;

public class NotificationsFragment extends Fragment implements RecyclerViewAdapterNotifications.OnItemClickedListener, RecyclerViewAdapterNotifications.OnContextMenuCreatedListener{

    private OnNotificationFragmentInteractionListener mListener;

    private DataViewModel viewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapterNotifications recyclerViewAdapter;

    private User user;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
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
        return inflater.inflate(R.layout.fragment_main_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = getView().findViewById(R.id.recyclerView_notifications);
        recyclerViewAdapter = new RecyclerViewAdapterNotifications(new ArrayList<Notification>(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        viewModel.getUsers().observe(NotificationsFragment.this, new Observer<List<User>>() {
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

        viewModel.getNotifications().observe(NotificationsFragment.this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                recyclerViewAdapter.updateItems(notifications);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNotificationFragmentInteractionListener) {
            mListener = (OnNotificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNotificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View v, int position) {
        mListener.onItemClickCustomNotifications(v, position);
    }

    @Override
    public void onContextMenuCreated(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, final int position) {
        menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Notification notification = recyclerViewAdapter.getItemByPosition(position);
                viewModel.deleteNotification(notification);
                return true;
            }
        });
    }

    public interface OnNotificationFragmentInteractionListener {
        void onItemClickCustomNotifications(View v, int position);
    }
}
