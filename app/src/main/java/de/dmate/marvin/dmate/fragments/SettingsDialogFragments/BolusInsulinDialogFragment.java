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
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

public class BolusInsulinDialogFragment extends DialogFragment {

    private OnBolusinsulinDialogFragmentInteractionListener mListener;

    private EditText editTextName;
    private EditText editTextDuration;

    private Button buttonCancel;
    private Button buttonConfirm;

    private DataViewModel viewModel;
    private User user;

    public BolusInsulinDialogFragment() {
        // Required empty public constructor
    }

    public static BolusInsulinDialogFragment newInstance() {
        BolusInsulinDialogFragment fragment = new BolusInsulinDialogFragment();
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
        return inflater.inflate(R.layout.fragment_settings_dialog_bolus_insulin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        editTextName = view.findViewById(R.id.editText_name_bolus);
        editTextDuration = view.findViewById(R.id.editText_duration_bolus);
        buttonCancel = view.findViewById(R.id.button_cancel_bolus);
        buttonConfirm = view.findViewById(R.id.button_confirm_bolus);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        //TESTING: delete all users
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                viewModel.deleteAllUsers();
//            }
//        }).start();

        viewModel.getUsers().observe(BolusInsulinDialogFragment.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable final List<User> users) {
//                initialize user
//                if user exists, initialize EditTexts with values from DB
//                if user does not exist, create new user
                try {
                    user = users.get(0);
                    if (user.getBolusName() != null) editTextName.setText(user.getBolusName());
                    if (user.getBolusDuration() != null) editTextDuration.setText(user.getBolusDuration().toString());
                } catch (IndexOutOfBoundsException e) {
                    user = new User();
                    viewModel.addUser(user);
                    Toast toast = Toast.makeText(getContext(), "Created new user", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        //set click listeners to buttons cancel and confirm
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setBolusName(editTextName.getText().toString());
                user.setBolusDuration(Float.parseFloat(editTextDuration.getText().toString()));
                viewModel.addUser(user);
                Toast toast = Toast.makeText(getContext(), "Updated bolus insulin information", Toast.LENGTH_LONG);
                toast.show();
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBolusinsulinDialogFragmentInteractionListener) {
            mListener = (OnBolusinsulinDialogFragmentInteractionListener) context;
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


    public interface OnBolusinsulinDialogFragmentInteractionListener {

    }
}
