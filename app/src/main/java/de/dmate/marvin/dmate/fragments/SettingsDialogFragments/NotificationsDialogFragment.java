package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

public class NotificationsDialogFragment extends DialogFragment {

    private OnNotificationsDialogFragmentInteractionListener mListener;

    private Switch switchEnabled;
    private Button buttonConfirm;

    private DataViewModel viewModel;
    private User user;

    public NotificationsDialogFragment() {
        // Required empty public constructor
    }

    public static NotificationsDialogFragment newInstance() {
        NotificationsDialogFragment fragment = new NotificationsDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //set new View to the builder and create the Dialog
        Dialog dialog = builder.setView(new View(getActivity())).create();

        //get WindowManager.LayoutParams, copy attributes from Dialog to LayoutParams and override them with MATCH_PARENT
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //show the Dialog before setting new LayoutParams to the Dialog
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_dialog_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        switchEnabled = view.findViewById(R.id.switch_notifications_enabled);
        buttonConfirm = view.findViewById(R.id.button_confirm_notification_settings);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        viewModel.getUsers().observe(NotificationsDialogFragment.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                try {
                    user = users.get(0);
                    switchEnabled.setChecked(user.getNotificationsEnabled());
                } catch (IndexOutOfBoundsException e) {
                    user = new User();
                    viewModel.addUser(user);
                    Toast toast = Toast.makeText(getContext(), "Created new user", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        switchEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setNotificationsEnabled(isChecked);
                viewModel.addUser(user);
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "Notification settings updated", Toast.LENGTH_LONG);
                toast.show();
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNotificationsDialogFragmentInteractionListener) {
            mListener = (OnNotificationsDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBasalinsulinDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNotificationsDialogFragmentInteractionListener {

    }
}
