package de.dmate.marvin.dmate.util;

import java.util.Comparator;

import de.dmate.marvin.dmate.entities.Entry;

/**
 * Created by Marvin on 17.02.2018.
 */

public class EntryComparator implements Comparator<Entry> {

    //compares dates and sorts entries (newest first)
    @Override
    public int compare(Entry e1, Entry e2) {
        return e2.getDate().compareTo(e1.getDate());
    }
}
