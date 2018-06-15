package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

public class ExportDialogFragment extends DialogFragment {

    private OnExportDialogFragmentInteractionListener mListener;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Button buttonGetPdf;

    private DataViewModel viewModel;
    private User user;
    private List<Daytime> daytimeList;
    private List<Sport> sportList;
    private List<PlannedBasalInjection> plannedBasalInjectionList;
    private List<Entry> entryList;
    private List<Exercise> exerciseList;

    private File myFile;

    public ExportDialogFragment() {
        // Required empty public constructor
    }

    public static ExportDialogFragment newInstance() {
        ExportDialogFragment fragment = new ExportDialogFragment();
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
        return inflater.inflate(R.layout.fragment_dialog_export, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            try {
                createPdf();
            } catch (FileNotFoundException | DocumentException e) {
                System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
            }
        }



        buttonGetPdf = view.findViewById(R.id.button_get_pdf);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        //start observing all lists that are needed for the export
        viewModel.getUsers().observe(ExportDialogFragment.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable final List<User> users) {
//                initialize user
//                if user exists, initialize EditTexts with values from DB
//                if user does not exist, create new user
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

        viewModel.getDaytimes().observe(ExportDialogFragment.this, new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                daytimeList = daytimes;
            }
        });

        viewModel.getSports().observe(ExportDialogFragment.this, new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                sportList = sports;
            }
        });

        viewModel.getPlannedBasalInjections().observe(ExportDialogFragment.this, new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {
                plannedBasalInjectionList = plannedBasalInjections;
            }
        });

        viewModel.getEntries().observe(ExportDialogFragment.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
            }
        });

        viewModel.getExercises().observe(ExportDialogFragment.this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                exerciseList = exercises;
            }
        });

        //set clicklistener to button
        buttonGetPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPdf();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExportDialogFragmentInteractionListener) {
            mListener = (OnExportDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBasalInsulineDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnExportDialogFragmentInteractionListener {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        createPdf();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                    viewPdf();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Please allow access to external storage to export pdf", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "dmate");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Toast toast = Toast.makeText(getContext(), "PDF directory created", Toast.LENGTH_LONG);
            toast.show();
        }

        myFile = new File(pdfFolder + "dmate" +  ".pdf");

        OutputStream output = new FileOutputStream(myFile);

        Rectangle pageSize = new Rectangle(216f, 720f);
        Document document = new Document(pageSize, 36f, 72f, 108f, 180f);

        PdfWriter.getInstance(document, output);
        document.open();

        document.add(new Paragraph("First paragraph"));
        document.add(new Paragraph("Second paragraph"));

        document.close();
    }

    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", myFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getContext().startActivity(intent);
    }
}


class createPdfTask extends AsyncTask<Document, Void, Document> {

    @Override
    protected Document doInBackground(Document... documents) {
        return null;
    }

    @Override
    protected void onPostExecute(Document document) {
        super.onPostExecute(document);
    }
}
