/*
 * Copyright 2015, Mostafa Sadraii
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

package com.msadraii.multidex.data;

import android.content.Context;

import com.msadraii.multidex.Entry;
import com.msadraii.multidex.EntryDao;

import java.util.ArrayList;
import java.util.Date;

/**
 * Provides an abstraction layer between the database and controllers for manipulating Entry
 * objects.
 * Created by Mostafa on 3/15/2015.
 */
public class EntryRepository {

    private static final String LOG_TAG = EntryRepository.class.getSimpleName();

    public static final Entry createEntry(Context context, Date date, String segments, long colorCodeId) {
        return new Entry(getNextId(context), date, segments, colorCodeId);
    }

    public static long getNextId(Context context) {
        ArrayList<Entry> entries = getAllEntries(context);
        return (entries == null) ? 0 : entries.size();
    }

    // returns the date entry of first application run.
    public static Date firstEntry(Context context) {
        if (getAllEntries(context).size() == 0) {
            Date date = new Date();
            createEntry(context, date, "", 0);
            return date;
        } else {
            return getEntryForId(context, 0).getDate();
        }
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
        return (ArrayList) getEntryDao(context).loadAll();
    }
    
    public static ArrayList<Entry> getEntryForDate(Context context, Date date) {
        // TODO: fill out getEntryForDate()
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