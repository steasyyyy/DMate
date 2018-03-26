package de.dmate.marvin.dmate.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

//ACCESS AND MANIPULATE DATA IN THE DATABASE FROM HERE
//create a new asynctask for every access type and call methods from EntryDao within it
public class EntryRoomListViewModel extends AndroidViewModel {

    private final LiveData<List<EntryRoom>> entries;

    private AppDatabase appDatabase;

    public EntryRoomListViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        entries = appDatabase.entryRoomDao().getAllEntries();
    }

    public LiveData<List<EntryRoom>> getEntries() {
        return entries;
    }

    //CALL THIS METHOD TO ADD AN ENTRY TO THE DATABASE
    public void addEntry(EntryRoom entryRoom) {
        new addAsyncTask(appDatabase).execute(entryRoom);
    }

    //CALL THIS METHOD TO DELETE AN ENTRY FROM THE DATABASE
    public void deleteEntry(EntryRoom entryRoom) {
        new deleteAsyncTask(appDatabase).execute(entryRoom);
    }

    public void getEntryById(int id) {
        new getEntryByIdAsyncTask(appDatabase).execute(id);
    }

    public void updateEntryRoom(EntryRoom entryRoom) {
        new updateEntryRoomAsyncTask(appDatabase).execute(entryRoom);
    }

    //AsyncTask for adding entries to the db
    private static class addAsyncTask extends AsyncTask<EntryRoom, Void, Void> {
        private AppDatabase db;

        addAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(EntryRoom... params) {
            db.entryRoomDao().insertEntryRoom(params[0]);
            return null;
        }
    }

    //AsyncTask for deleting entries from the db
    private static class deleteAsyncTask extends AsyncTask<EntryRoom, Void, Void> {
        private AppDatabase db;

        deleteAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(final EntryRoom... params) {
            db.entryRoomDao().deleteEntryRoom(params[0]);
            return null;
        }
    }

    //AsyncTask for getting an entry by id
    private static class getEntryByIdAsyncTask extends AsyncTask<Integer, Void, EntryRoom> {
        private AppDatabase db;

        getEntryByIdAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected EntryRoom doInBackground(Integer... params) {
            return db.entryRoomDao().getItemById((params[0]));
        }
    }

    private static class updateEntryRoomAsyncTask extends AsyncTask<EntryRoom, Void, Void> {
        private AppDatabase db;

        updateEntryRoomAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(EntryRoom... params) {
            db.entryRoomDao().updateEntryRoom(params[0]);
            return null;
        }
    }
}
