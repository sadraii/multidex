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

package com.sadraii;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.sadraii.hyperdex.ColorCode;
import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.R;
import com.sadraii.hyperdex.colorcode.EditColorCodeActivity;
import com.sadraii.hyperdex.data.ColorCodeRepository;
import com.sadraii.hyperdex.data.EntryRepository;
import com.sadraii.hyperdex.data.GreenDaoApplication;
import com.sadraii.hyperdex.dialogues.DatePickerFragment;
import com.sadraii.hyperdex.entry.EntryFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DATE_DIALOGUE_FRAGMENT_TAG = "date_dialogue_fragment";

    private Context mAppContext;
    private HyperdexAdapter mAdapter;
    private ViewPager mPager;
    private int selectedColorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAppContext = this.getApplicationContext();

        DEBUG_firstInstall(true);

        mAdapter = new HyperdexAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.hyperdex_pager);
        mPager.setAdapter(mAdapter);
        // TODO: today's date. If no savedinstancestate, go to today's date
        if (savedInstanceState != null) {

        } else {
            mPager.setCurrentItem(getEntryIdForToday(), false);
        }
        selectedColorCode = 0;

//        clearTables();
//        recreateTables();
//        addTestColors();
//        addTestEntries();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                ColorCodeRepository.getAllTasks(mAppContext));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.color_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColorCode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void DEBUG_firstInstall(boolean firstInstall) {
        if (firstInstall) {
            // TODO: probably don't do this in case of uninstall/reinstall, check if db exists first
            recreateTables();
            ColorCodeRepository.addNextColorCode(mAppContext, "#ff33b5e5", "0 Make breakfast");
            EntryRepository.addNextEntry(mAppContext, Calendar.getInstance().getTime(), "", 0);
        }
    }

    // TODO: test 2 times for same day. test different days, but plus/minus 24 hours on the current day. should not add 1 day if more than 24hrs, go by DATE not TIME

    /**
     * Returns the ID of the {@link Entry} for today.
     *
     * @return The ID of the {@link Entry} for today.
     */
    private int getEntryIdForToday() {
        Entry entry = EntryRepository.getFirstEntry(mAppContext);
        if (entry != null) {
            Date first = entry.getDate();
            Date today = Calendar.getInstance().getTime();
            long difference = today.getTime() - first.getTime();
            return (int) TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        }
        return 0;
    }

    /**
     * Returns the currently selected {@link ColorCode} ID of the {@link Spinner}.
     *
     * @return  The currently selected {@link ColorCode} ID.
     */
    public int getSelectedColorCode() {
        return selectedColorCode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_calendar: {
                showDatePicker();
                return true;
            }
            case R.id.action_settings: {
                // TODO: settings activity
                return true;
            }
            case R.id.action_edit_label: {
                startActivity(new Intent(this, EditColorCodeActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mPager.setCurrentItem(daysSinceFirstEntry(year, monthOfYear, dayOfMonth));
            }
        });
        newFragment.show(getSupportFragmentManager(),
                DATE_DIALOGUE_FRAGMENT_TAG);
    }

    @SuppressWarnings("deprecation")
    private int daysSinceFirstEntry(int year, int monthOfYear, int dayOfMonth) {
        Entry firstEntry = EntryRepository.getFirstEntry(mAppContext);
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstEntry.getDate());
        int firstDay = cal.get(Calendar.DAY_OF_YEAR);
        int firstYear = cal.get(Calendar.YEAR);

        cal.setTime(new Date(year - 1900, monthOfYear, dayOfMonth));
        int currentDay = cal.get(Calendar.DAY_OF_YEAR);
        int currentYear = cal.get(Calendar.YEAR);

        int differenceInDays = 0;

        if (firstYear == currentYear) {
            differenceInDays = currentDay - firstDay; // - 1 needed?
        } else {
            for (int i = currentYear; i > firstYear; i--) {
                // Account for leap year
                int daysInYear = (i % 4 == 0) ? 366 : 365;

                if (firstYear == i - 1) {
                    // subtract days from year and exit
                    differenceInDays += daysInYear - firstDay + currentDay;
                } else {
                    differenceInDays += daysInYear;
                }
            }
        }
        return differenceInDays;
    }

    /**
     * Retrieve the current {@link PagerAdapter}.
     *
     * @return  The currently registered {@link PagerAdapter}.
     */
    public PagerAdapter getPagerAdapter() {
        return mAdapter;
    }

    /**
     * Retrieve the current {@link ViewPager}.
     *
     * @return  The currently registered {@link ViewPager}.
     */
    public ViewPager getViewPager() {
        return mPager;
    }

    private void recreateTables() {
        ColorCodeRepository.dropTable(mAppContext);
        EntryRepository.dropTable(mAppContext);
        ColorCodeRepository.createTable(mAppContext);
        EntryRepository.createTable(mAppContext);
    }

    private void clearTables() {
        ColorCodeRepository.clearColorCodes(mAppContext);
        EntryRepository.clearEntries(mAppContext);
    }

    public static class HyperdexAdapter extends FragmentStatePagerAdapter {
        private Context appContext = GreenDaoApplication.getAppContext();
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public HyperdexAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            // Space for 10 years of entries
            return 3650;
        }

        @Override
        public Fragment getItem(int position) {
            return EntryFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO: fix getTime()
            // If user scrolled to the last entry, create a new one for the next day
            // If there is a gap between last entry date and today's date, create entries until
            // tomorrow's date.
            int size = EntryRepository.getAllEntries(appContext).size();
            Date lastEntryDate = EntryRepository.getEntryForId(appContext, size - 1).getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastEntryDate);

            if (position == size - 1) {
                cal.add(Calendar.DATE, 1);
                EntryRepository.addNextEntry(appContext, cal.getTime(), "", 0);
            } else if (position >= size) {
                while (EntryRepository.getAllEntries(appContext).size() < position + 2) {
                    cal.add(Calendar.DATE, 1);
                    EntryRepository.addNextEntry(appContext, cal.getTime(), "", 0);
                }
            }

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    private void addTestEntries() {
        Calendar cal = Calendar.getInstance();
        Gson gson = new Gson();

        HashMap<Integer, Integer> entrySegments = new HashMap<>();
        entrySegments.put(72, 4);
        entrySegments.put(67, 2);
        entrySegments.put(7, 3);
        entrySegments.put(10, 1);
        Log.d("gson= ", gson.toJson(entrySegments));
        EntryRepository.insertOrReplace(mAppContext, EntryRepository.initEntry(
                mAppContext,
                cal.getTime(),
                gson.toJson(entrySegments),
                0
        ));

        entrySegments = new HashMap<>();
        entrySegments.put(2, 1);
        entrySegments.put(3, 0);
        entrySegments.put(30, 4);
        Log.d("gson= ", gson.toJson(entrySegments));
        cal.add(Calendar.DATE, 1);
        EntryRepository.insertOrReplace(mAppContext, EntryRepository.initEntry(
                mAppContext,
                cal.getTime(),
                gson.toJson(entrySegments),
                1
        ));

        entrySegments = new HashMap<>();
        entrySegments.put(27, 2);
        entrySegments.put(28, 3);
        entrySegments.put(29, 1);
        Log.d("gson= ", gson.toJson(entrySegments));
        cal.add(Calendar.DATE, 1);
        EntryRepository.insertOrReplace(mAppContext, EntryRepository.initEntry(
                mAppContext,
                cal.getTime(),
                gson.toJson(entrySegments),
                2
        ));
        mAdapter.notifyDataSetChanged();
    }

    private void addTestColors() {
        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
                mAppContext,
                "#ff33b5e5",
                "0 Make breakfast"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
                mAppContext,
                "#ffaa66cc",
                "1 Jump rope"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
                mAppContext,
                "#ff99cc00",
                "2 Wash dishes"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
                mAppContext,
                "#ffffbb33",
                "3 Complain"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
                mAppContext,
                "#ffff4444",
                "4 How long is this text box and does it really wrap around or not?"
        ));
    }
}