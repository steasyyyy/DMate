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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;
import de.dmate.marvin.dmate.util.Helper;

public class ExportDialogFragment extends DialogFragment {

    private OnExportDialogFragmentInteractionListener mListener;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Boolean userAdded = false;
    private Boolean daytimesAdded = false;
    private Boolean sportsAdded = false;
    private Boolean plannedBasalInjectionsAdded = false;
    private Boolean exercisesAdded = false;
    private Boolean entriesAdded = false;

    private Boolean permissionGranted = true;

    private Boolean documentCreated = false;

    private Button buttonGetPdf;

    private DataViewModel viewModel;
    private User user;
    private List<Daytime> daytimeList;
    private List<Sport> sportList;
    private List<PlannedBasalInjection> plannedBasalInjectionList;
    private List<Exercise> exerciseList;
    private List<Entry> entryList;

    private File myFile;
    private Document document;

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
            permissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        buttonGetPdf = view.findViewById(R.id.button_get_pdf);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        //start observing all lists that are needed for the export
        viewModel.getUsers().observe(ExportDialogFragment.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable final List<User> users) {
//                initialize user
//                if user does not exist, create new user
                try {
                    user = users.get(0);
                    userAdded = true;
                    if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded) {
                        try {
                            createDocument();
                        } catch (FileNotFoundException | DocumentException e) {
                            System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                        }
                    }
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
                daytimesAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getSports().observe(ExportDialogFragment.this, new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                sportList = sports;
                sportsAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getPlannedBasalInjections().observe(ExportDialogFragment.this, new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {
                plannedBasalInjectionList = plannedBasalInjections;
                plannedBasalInjectionsAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getExercises().observe(ExportDialogFragment.this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                exerciseList = exercises;
                exercisesAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getEntries().observe(ExportDialogFragment.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                entriesAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        //set clicklistener to button
        buttonGetPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentCreated) viewPdf();
                else {
                    Toast toast = Toast.makeText(getContext(), "Please wait for the document to be created", Toast.LENGTH_LONG);
                    toast.show();
                }
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
                    permissionGranted = true;
                } else {
                    Toast toast = Toast.makeText(getContext(), "Please allow access to external storage to export pdf", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void createDocument() throws FileNotFoundException, DocumentException {
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "dmate");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Toast toast = Toast.makeText(getContext(), "PDF directory created", Toast.LENGTH_LONG);
            toast.show();
        }

        myFile = new File(pdfFolder +  ".pdf");

        OutputStream output = new FileOutputStream(myFile, false);

        document = new Document();

        PdfWriter.getInstance(document, output);
        document.open();

        //set header including user name
        if (user.getName() != null) {
            Paragraph p1 = new Paragraph("DMate export for " + user.getName());
            p1.setSpacingAfter(72f);
            document.add(p1);
        }
        else {
            Paragraph p1 = new Paragraph("DMate export");
            p1.setSpacingAfter(72f);
            document.add(p1);
        }

        //set date
        String date = Helper.formatMillisToDateString(System.currentTimeMillis());
        Paragraph p2 = new Paragraph(date);
        p2.setSpacingAfter(72f);
        document.add(p2);



        //create tableUserInfo
        PdfPTable tableUserInfo = new PdfPTable(new float[]{1, 1});
        tableUserInfo.setHeaderRows(1);
        tableUserInfo.setSpacingAfter(72f);

        //set user name
        tableUserInfo.addCell("User name");
        if (user.getName() != null) tableUserInfo.addCell(user.getName());
        else tableUserInfo.addCell("");

        //set bolus insuline information
        tableUserInfo.addCell("Bolus insuline name");
        if (user.getBolusName() != null) tableUserInfo.addCell(user.getBolusName());
        else tableUserInfo.addCell("");
        tableUserInfo.addCell("Bolus insuline duration of action");
        if (user.getBolusDuration() != null) tableUserInfo.addCell(user.getBolusDuration().toString());
        else tableUserInfo.addCell("");

        //set basal insuline information
        tableUserInfo.addCell("Basal insuline name");
        if (user.getBasalName() != null) tableUserInfo.addCell(user.getBasalName());
        else tableUserInfo.addCell("");
        tableUserInfo.addCell("Basal insuline duration of action");
        if (user.getBasalDuration() != null) tableUserInfo.addCell(user.getBasalDuration().toString());
        else tableUserInfo.addCell("");

        //set target area
        tableUserInfo.addCell("Lower end of target area");
        if (user.getTargetMin() != null) tableUserInfo.addCell(user.getTargetMin().toString());
        else tableUserInfo.addCell("");
        tableUserInfo.addCell("Upper end of target area");
        if (user.getTargetMax() != null) tableUserInfo.addCell(user.getTargetMax().toString());
        else tableUserInfo.addCell("");

        //set units
        tableUserInfo.addCell("Unit for carbohydrates");
        if (user.getUnitBu()) tableUserInfo.addCell("Bread units");
        else tableUserInfo.addCell("Carbohydrate units");
        tableUserInfo.addCell("Unit for blood sugar values");
        if (user.getUnitMgdl()) tableUserInfo.addCell("mg/dl");
        else tableUserInfo.addCell("mmol/l");

        //add user information table to document
        document.add(tableUserInfo);


        //create planned basal injections table
        PdfPTable tablePbi = new PdfPTable(new float[]{1,1});
        tablePbi.setHeaderRows(1);
        tablePbi.setSpacingAfter(72f);

        if (plannedBasalInjectionList.size() == 0) {
            tablePbi.addCell("No planned basal insuline injections defined");
            tablePbi.addCell("-");
            tablePbi.addCell("-");
            tablePbi.addCell("-");
        } else {
            int counter = 1;
            for (PlannedBasalInjection pbi : plannedBasalInjectionList) {
                PdfPCell cellDaytimeColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellDaytimeColored.setBackgroundColor(color);
                cellDaytimeColored.setPhrase(new Phrase("Planned basal insuline injection " + counter));
                tablePbi.addCell(cellDaytimeColored);
                cellDaytimeColored.setPhrase(new Phrase(""));
                tablePbi.addCell(cellDaytimeColored);
                tablePbi.addCell("Time of day");
                tablePbi.addCell(pbi.getTimeOfDay());
                tablePbi.addCell("Basal insuline injection");
                tablePbi.addCell(pbi.getBasal().toString());
                counter++;
            }
        }

        //add pbiTable to document
        document.add(tablePbi);


        //create table daytimes
        PdfPTable tableDaytimes = new PdfPTable(new float[]{1,1});
        tableDaytimes.setHeaderRows(1);
        tableDaytimes.setSpacingAfter(72f);

        if (daytimeList.size() == 0) {
            tableDaytimes.addCell("No daytimes defined");
            tableDaytimes.addCell("-");
            tableDaytimes.addCell("-");
            tableDaytimes.addCell("-");
        } else {
            int counter = 1;
            for (Daytime d : daytimeList) {
                PdfPCell cellDaytimeColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellDaytimeColored.setBackgroundColor(color);
                cellDaytimeColored.setPhrase(new Phrase("Daytime " + counter));
                tableDaytimes.addCell(cellDaytimeColored);
                cellDaytimeColored.setPhrase(new Phrase(""));
                tableDaytimes.addCell(cellDaytimeColored);
                tableDaytimes.addCell("Start of daytime");
                tableDaytimes.addCell(d.getDaytimeStart());
                tableDaytimes.addCell("End of daytime");
                tableDaytimes.addCell(d.getDaytimeEnd());
                tableDaytimes.addCell("Bread unit factor");
                tableDaytimes.addCell(d.getBuFactor().toString());
                tableDaytimes.addCell("Correction factor");
                tableDaytimes.addCell(d.getCorrectionFactor().toString());

                counter++;
            }
        }

        document.add(tableDaytimes);


        //create table sports
        PdfPTable tableSports = new PdfPTable(new float[]{1,1});
        tableSports.setHeaderRows(1);
        tableSports.setSpacingAfter(72f);

        if (sportList.size() == 0) {
            tableSports.addCell("No sportive activities defined");
            tableSports.addCell("-");
            tableSports.addCell("-");
            tableSports.addCell("-");
        } else {
            int counter = 1;
            for (Sport s : sportList) {
                PdfPCell cellSportColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellSportColored.setBackgroundColor(color);
                cellSportColored.setPhrase(new Phrase("Sportive activity " + counter));
                tableSports.addCell(cellSportColored);
                cellSportColored.setPhrase(new Phrase(""));
                tableSports.addCell(cellSportColored);
                tableSports.addCell("Name");
                tableSports.addCell(s.getSportName());
                tableSports.addCell("Effect per unit");
                tableSports.addCell(s.getSportEffectPerUnit().toString());
                counter++;
            }
        }

        document.add(tableSports);


        //create table entries
        PdfPTable tableEntries = new PdfPTable(new float[]{1,1});
        tableEntries.setHeaderRows(1);
        tableEntries.setSpacingAfter(72f);

        if (entryList.size() == 0) {
            tableEntries.addCell("No entries available");
            tableEntries.addCell("-");
            tableEntries.addCell("-");
            tableEntries.addCell("-");
        } else {
            int counter = 1;
            for (Entry e : entryList) {
                PdfPCell cellEntryColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellEntryColored.setBackgroundColor(color);
                cellEntryColored.setPhrase(new Phrase("Entry " + counter));
                tableEntries.addCell(cellEntryColored);
                cellEntryColored.setPhrase(new Phrase(""));
                tableEntries.addCell(cellEntryColored);
                tableEntries.addCell("Date");
                tableEntries.addCell(Helper.formatMillisToDateString(e.getTimestamp().getTime()));
                tableEntries.addCell("Time");
                tableEntries.addCell(Helper.formatMillisToTimeString(e.getTimestamp().getTime()));
                tableEntries.addCell("Blood sugar");
                if (e.getBloodSugar() != null) tableEntries.addCell(e.getBloodSugar().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Bread units / Carb units");
                if (e.getBreadUnit() != null) tableEntries.addCell(e.getBreadUnit().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Bolus insulin injection");
                if (e.getBolus() != null) tableEntries.addCell(e.getBolus().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Basal insuline injection");
                if (e.getBasal() != null) tableEntries.addCell(e.getBasal().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Note");
                if (e.getNote() != null) tableEntries.addCell(e.getNote());
                else tableEntries.addCell("-");
                tableEntries.addCell("Diseased");
                if (e.getReliable() != null) tableEntries.addCell(e.getReliable().toString());
                else tableEntries.addCell("-");

                int counterEx = 1;
                for (Exercise ex : exerciseList) {
                    if (e.geteId().equals(ex.geteIdF())) {
                        for (Sport s : sportList) {
                            if (s.getsId().equals(ex.getsIdF())) {
                                tableEntries.addCell("Exercise " + counterEx);
                                tableEntries.addCell("Kind of sport");
                                tableEntries.addCell(s.getSportName());
                                tableEntries.addCell("Effect per unit");
                                tableEntries.addCell(s.getSportEffectPerUnit().toString());
                                tableEntries.addCell("Units of sport");
                                tableEntries.addCell(ex.getExerciseUnits().toString());
                            }
                        }
                    }
                    counterEx++;
                }
                counter++;
            }
        }

        document.add(tableEntries);




        //close document
        document.close();

        documentCreated = true;
    }

    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
