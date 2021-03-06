/*
 * Copyright 2015 Mostafa Sadraii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sadraii.hyperdex.data;

import android.content.Context;

import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.EntryDao;

import java.util.ArrayList;
import java.util.Date;

/**
 * Provides an abstraction layer between the database and controllers for manipulating Entry
 * objects.
 */
public class EntryRepository {

    private static final String LOG_TAG = EntryRepository.class.getSimpleName();

    /**
     * Creates, inserts, and returns the next available {@link Entry}.
     *
     * @param context
     * @param date
     * @param segments
     * @return
     */
    public static Entry addNextEntry(Context context, Date date, String segments) {
        Entry entry = initEntry(context, date, segments);
        insertOrReplace(context, entry);
        return entry;
    }


    /**
     * Creates and returns an {@link Entry} set to the next available ID.
     *
     * @param context
     * @param date
     * @param segments
     * @return
     */
    public static Entry initEntry(Context context, Date date, String segments) {
        return new Entry(getNextId(context), date, segments);
    }

    /**
     * Returns the next unused ID.
     *
     * @param context
     * @return
     */
    public static long getNextId(Context context) {
        ArrayList<Entry> entries = getAllEntries(context);
        return (entries == null) ? 0 : entries.size();
    }

    /**
     * Returns the first {@link Entry} or <code>null</code> if no entry exists.
     *
     * @param context
     * @return
     */
    public static Entry getFirstEntry(Context context) {
        return (size(context) == 0) ? null : getEntryForId(context, 0);
    }

    /**
     * Returns the total number of entries.
     *
     * @param context
     * @return
     */
    public static int size(Context context) {
        return (int) getEntryDao(context).count();
    }

    public static void insertOrReplace(Context context, Entry entry) {
        getEntryDao(context).insertOrReplace(entry);
    }

    public static void clearEntries(Context context) {
        getEntryDao(context).deleteAll();
    }

    public static void deleteEntryWithId(Context context, long id) {
        getEntryDao(context).delete(getEntryForId(context, id));
    }

    public static ArrayList<Entry> getAllEntries(Context context) {
        return (ArrayList<Entry>) getEntryDao(context).loadAll();
    }

    public static Entry getEntryForId(Context context, long id) {
        return getEntryDao(context).load(id);
    }

    public static void dropTable(Context context) {
        getEntryDao(context).dropTable(
                ((GreenDaoApplication) context).getDaoSession().getDatabase(),
                true);
    }

    public static void createTable(Context context) {
        getEntryDao(context).createTable(
                ((GreenDaoApplication) context).getDaoSession().getDatabase(),
                true);
    }

    private static EntryDao getEntryDao(Context context) {
        return ((GreenDaoApplication) context).getDaoSession().getEntryDao();
    }
}