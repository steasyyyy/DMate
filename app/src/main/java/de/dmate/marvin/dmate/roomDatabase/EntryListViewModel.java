package de.dmate.marvin.dmate.roomDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

//ACCESS AND MANIPULATE DATA IN THE DATABASE FROM HERE
//create a new asynctask for every access type and call methods from EntryDao within it
public class EntryListViewModel extends AndroidViewModel {

    private final LiveData<List<Entry>> entries;

    private AppDatabase appDatabase;

    public EntryListViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        entries = appDatabase.entryRoomDao().getAllEntries();
    }

    public LiveData<List<Entry>> getEntries() {
        return entries;
    }

    //CALL THIS METHOD TO ADD AN ENTRY TO THE DATABASE
    public void addEntry(Entry entry) {
        new addEntryAsyncTask(appDatabase).execute(entry);
    }

    //CALL THIS METHOD TO DELETE AN ENTRY FROM THE DATABASE
    public void deleteEntry(Entry entry) {
        new deleteEntryAsyncTask(appDatabase).execute(entry);
    }

    //TODO VOID????
    //CALL THIS METHOD TO GET AN ENTRY BY ID
    public void getEntryById(int id) {
        new getEntryByIdAsyncTask(appDatabase).execute(id);
    }

    //CALL THIS METHOD TO UPDATE AN EMTRY BY OBJECT REFERENCE
    public void updateEntry(Entry entry) {
        new updateEntryAsyncTask(appDatabase).execute(entry);
    }

    //AsyncTask for adding entries to the db
    private static class addEntryAsyncTask extends AsyncTask<Entry, Void, Void> {
        private AppDatabase db;

        addEntryAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Entry... params) {
            db.entryRoomDao().insertEntryRoom(params[0]);
            return null;
        }
    }

    //AsyncTask for deleting entries from the db
    private static class deleteEntryAsyncTask extends AsyncTask<Entry, Void, Void> {
        private AppDatabase db;

        deleteEntryAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(final Entry... params) {
            db.entryRoomDao().deleteEntryRoom(params[0]);
            return null;
        }
    }

    //AsyncTask for getting an entry by id
    private static class getEntryByIdAsyncTask extends AsyncTask<Integer, Void, Entry> {
        private AppDatabase db;

        getEntryByIdAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Entry doInBackground(Integer... params) {
            return db.entryRoomDao().getItemById((params[0]));
        }
    }

    //AsyncTask for updating an Entry
    private static class updateEntryAsyncTask extends AsyncTask<Entry, Void, Void> {
        private AppDatabase db;

        updateEntryAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Entry... params) {
            db.entryRoomDao().updateEntryRoom(params[0]);
            return null;
        }
    }
}
