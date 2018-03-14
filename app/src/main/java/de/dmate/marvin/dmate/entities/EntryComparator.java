package de.dmate.marvin.dmate.entities;

import java.util.Comparator;

import de.dmate.marvin.dmate.entities.Entry;

public class EntryComparator implements Comparator<Entry> {

    //compares dates and sorts entries (newest first)
    @Override
    public int compare(Entry e1, Entry e2) {
        return e2.getDateMillis().compareTo(e1.getDateMillis());
    }
}
