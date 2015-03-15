package com.msadraii.multidex;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class EntryRepository {

    public static void insertOrReplace(Context context, Entry Entry) {
        getEntryDao(context).insertOrReplace(Entry);
    }

    public static void clearEntries(Context context) {
        getEntryDao(context).deleteAll();
    }

    public static void deleteEntryWithId(Context context, long id) {
        getEntryDao(context).delete(getEntryForId(context, id));
    }

    public static ArrayList<Entry> getAllEntries(Context context) {
        return (ArrayList) getEntryDao(context).loadAll();
    }
    
    public static ArrayList<Entry> getAllEntriesForDate(Context context, Date date, boolean isAM) {
        // TODO fill out getAllEntriesForDate()
        return null;
    }

    public static Entry getEntryForId(Context context, long id) {
        return getEntryDao(context).load(id);
    }

    public static void dropTable(Context context) {
        getEntryDao(context).dropTable(
                ((GreenDaoApplication) context.getApplicationContext()).getDaoSession().getDatabase(),
                true);
    }

    public static void createTable(Context context) {
        getEntryDao(context).createTable(
                ((GreenDaoApplication) context.getApplicationContext()).getDaoSession().getDatabase(),
                true);
    }

    private static EntryDao getEntryDao(Context context) {
        return ((GreenDaoApplication) context.getApplicationContext()).getDaoSession().getEntryDao();
    }
}