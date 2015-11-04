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

package com.sadraii.hyperdex;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sadraii.hyperdex.colorcode.ColorCodeActivity;
import com.sadraii.hyperdex.data.ColorCodeRepository;
import com.sadraii.hyperdex.data.EntryRepository;
import com.sadraii.hyperdex.data.GreenDaoApplication;
import com.sadraii.hyperdex.dialogues.DatePickerFragment;
import com.sadraii.hyperdex.entry.EntryFragment;
import com.sadraii.hyperdex.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO: editText
 */
public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DATE_DIALOGUE_FRAGMENT_TAG = "date_dialogue_fragment";
    private static final String SELECTED_COLOR_CODE = "selected_color_code";
    private static final String CURRENT_PAGER_ITEM = "current_pager_item";
    private static final int SPINNER_VIEW_TYPE = 0;
    private static final int SPINNER_DROP_DOWN_VIEW_TYPE = 1;
    // Used in PagerAdapter for infinite scrolling and date transitions
    private static final int MAX_ENTRY_CAPACITY = 3650;

    private Context mAppContext;
    private HyperdexAdapter mAdapter;
    private ViewPager mPager;
    private Spinner mSpinner;
    private int mSelectedColorCode;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.fragment_pager);
        mAppContext = this.getApplicationContext();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean firstRunDefault = getResources().getBoolean(R.bool.pref_first_run_setup_default);
        boolean isFirstRun = sharedPref.getBoolean(
                getString(R.string.pref_first_run_setup_key), firstRunDefault);
        if (isFirstRun) {
            onFirstRun();
        }

        mAdapter = new HyperdexAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.hyperdex_pager);
        mPager.setAdapter(mAdapter);

        // Go to the last date viewed, or to today's date
        int entryId = Utils.getEntryIdForToday(mAppContext);
        if (inState != null) {
            mPager.setCurrentItem(inState.getInt(CURRENT_PAGER_ITEM, entryId), false);
            mSelectedColorCode = inState.getInt(SELECTED_COLOR_CODE, 0);
        } else {
            mSelectedColorCode = sharedPref.getInt(getString(R.string.pref_selected_color_code), 0);
            int current_item = sharedPref.getInt(getString(R.string.pref_current_pager_item),
                    entryId);
            mPager.setCurrentItem(current_item, false);
        }

        mSpinner = (Spinner) findViewById(R.id.color_spinner);
        updateSpinnerAdapter();
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedColorCode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSpinnerAdapter();
    }

    /**
     * Set color descriptions and ints used by spinner adapter
     */
    private void updateSpinnerAdapter() {
        ArrayList<String> colorDescriptions = ColorCodeRepository.getAllTasks(mAppContext);
        ArrayList<Integer> colorInts = ColorCodeRepository.getAllColorInts(mAppContext);
        ColorSpinnerAdapter mSpinnerAdapter = new ColorSpinnerAdapter(this,
                android.R.layout.simple_spinner_item, colorDescriptions, colorInts);
        // If color code that was previously selected has since been deleted, reset to first
        // color code as fallback
        if (colorInts.size() <= mSelectedColorCode) {
            mSelectedColorCode = 0;
        }
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(mSelectedColorCode, false);
    }

    private void onFirstRun() {
        createTables();
        ColorCodeRepository.insertOrReplace(mAppContext, new ColorCode(0L, 1428021648563L, "#fffe9700", "Work"));
        ColorCodeRepository.insertOrReplace(mAppContext, new ColorCode(1L, 1428021648568L, "#ffccdb38", "Errands"));
        ColorCodeRepository.insertOrReplace(mAppContext, new ColorCode(2L, 1428021648574L, "#ff2095f2", "Relaxing"));
        EntryRepository.addNextEntry(mAppContext, Calendar.getInstance().getTime(),
                "{\"58\":1428021648568,\"55\":1428021648563,\"57\":1428021648563,\"56\":1428021648563,\"61\":1428021648574,\"59\":1428021648568}");
        getPreferences(Context.MODE_PRIVATE).edit()
                .putBoolean(getString(R.string.pref_first_run_setup_key), false)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_COLOR_CODE, mSelectedColorCode);
        outState.putInt(CURRENT_PAGER_ITEM, mPager.getCurrentItem());
    }
    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(Context.MODE_PRIVATE).edit()
                .putInt(getString(R.string.pref_selected_color_code), mSelectedColorCode)
                .putInt(getString(R.string.pref_current_pager_item), mPager.getCurrentItem())
                .commit();
    }

    /**
     * Returns the currently selected {@link ColorCode} ID of the {@link Spinner}.
     *
     * @return  The currently selected {@link ColorCode} ID.
     */
    public int getSelectedColorCodeId() {
        return mSelectedColorCode;
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
            case R.id.action_settings: { // TODO: remove settings?
                return true;
            }
            case R.id.action_edit_label: {
                startActivity(new Intent(this, ColorCodeActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show the {@link DatePickerFragment} and scroll the {@link ViewPager} to the date picked.
     */
    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mPager.setCurrentItem(Utils.daysSinceFirstEntry(mAppContext, year, month,
                        day));
            }
        });
        newFragment.show(getSupportFragmentManager(),
                DATE_DIALOGUE_FRAGMENT_TAG);
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

    private void createTables() {
        ColorCodeRepository.dropTable(mAppContext);
        EntryRepository.dropTable(mAppContext);

        ColorCodeRepository.createTable(mAppContext);
        EntryRepository.createTable(mAppContext);
    }

    private void clearTables() {
        ColorCodeRepository.clearColorCodes(mAppContext);
        EntryRepository.clearEntries(mAppContext);
    }

    /**
     * Allows for continuous scrolling through Entries by creating fragments on the fly.
     */
    private static class HyperdexAdapter extends FragmentStatePagerAdapter {

        private final Context appContext = GreenDaoApplication.getAppContext();
        private final SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public HyperdexAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        /**
         * Returns enough space for 10 years of entries.
         *
         * @return  Space for 10 years of entries.
         */
        @Override
        public int getCount() {
            return MAX_ENTRY_CAPACITY;
        }

        @Override
        public Fragment getItem(int position) {
            return EntryFragment.newInstance(position);
        }

        /**
         * Creates extra {@link Entry} objects in the DB to always have one more than the current
         * Entry for seamless scrolling. If there is a gap between the last Entry in DB and the
         * current Entry, it fills that gap with Entries and creates one for tomorrow.
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int size = EntryRepository.size(appContext);
            Date lastEntryDate = EntryRepository.getEntryForId(appContext, size - 1).getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastEntryDate);
            // If user scrolled to the last entry, create a new one for the next day
            // If there is a gap between last entry date and today's date, create entries until
            // tomorrow's date.
            if (position == size - 1) {
                cal.add(Calendar.DATE, 1);
                EntryRepository.addNextEntry(appContext, cal.getTime(), "");
            } else if (position >= size) {
                while (EntryRepository.size(appContext) < position + 2) {
                    cal.add(Calendar.DATE, 1);
                    EntryRepository.addNextEntry(appContext, cal.getTime(), "");
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
    }

    /**
     * Provides spinner view with color circles and descriptions.
     */
    private class ColorSpinnerAdapter extends ArrayAdapter<String> {

        final ArrayList<String> descriptions;
        final ArrayList<Integer> colors;

        public ColorSpinnerAdapter(Context context, int resource, ArrayList<String> descriptions,
                                   ArrayList<Integer> colors) {
            super(context, resource, descriptions);
            this.descriptions = descriptions;
            this.colors = colors;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getSpinnerView(position, convertView, parent, SPINNER_VIEW_TYPE);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getSpinnerView(position, convertView, parent, SPINNER_DROP_DOWN_VIEW_TYPE);
        }

        public View getSpinnerView(int position, View convertView, ViewGroup parent, int type) {
            View spinnerView = convertView;
            if (spinnerView == null) {
                spinnerView = getLayoutInflater().inflate(R.layout.spinner_row, parent, false);
            }
            if (type == SPINNER_VIEW_TYPE) { // TODO: const color/style
//                spinnerView.setBackgroundColor(Color.parseColor("#ffdddddd"));
            }
            ImageView colorView = (ImageView) spinnerView.findViewById(R.id.spinner_color_image_view);
            ((GradientDrawable) colorView.getBackground()).setColor(colors.get(position));

            TextView description = (TextView) spinnerView.findViewById(R.id.spinner_description);
            description.setText(descriptions.get(position));

            return spinnerView;
        }
    }

//    private void addTestEntries() {
//        Calendar cal = Calendar.getInstance();
//        Gson gson = new Gson();
//
//        HashMap<Integer, Integer> entrySegments = new HashMap<>();
//        entrySegments.put(72, 4);
//        entrySegments.put(67, 2);
//        entrySegments.put(7, 3);
//        entrySegments.put(10, 1);
//        Log.d("gson= ", gson.toJson(entrySegments));
//        EntryRepository.insertOrReplace(mAppContext, EntryRepository.initEntry(
//                mAppContext,
//                cal.getTime(),
//                gson.toJson(entrySegments)
//        ));
//
//        entrySegments = new HashMap<>();
//        entrySegments.put(2, 1);
//        entrySegments.put(3, 0);
//        entrySegments.put(30, 4);
//        Log.d("gson= ", gson.toJson(entrySegments));
//        cal.add(Calendar.DATE, 1);
//        EntryRepository.insertOrReplace(mAppContext, EntryRepository.initEntry(
//                mAppContext,
//                cal.getTime(),
//                gson.toJson(entrySegments)
//        ));
//
//        entrySegments = new HashMap<>();
//        entrySegments.put(27, 2);
//        entrySegments.put(28, 3);
//        entrySegments.put(29, 1);
//        Log.d("gson= ", gson.toJson(entrySegments));
//        cal.add(Calendar.DATE, 1);
//        EntryRepository.insertOrReplace(mAppContext, EntryRepository.initEntry(
//                mAppContext,
//                cal.getTime(),
//                gson.toJson(entrySegments)
//        ));
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private void addTestColors() {
//        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
//                mAppContext,
//                "#ff33b5e5",
//                "0 Make breakfast"
//        ));
//
//        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
//                mAppContext,
//                "#ffaa66cc",
//                "1 Jump rope"
//        ));
//
//        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
//                mAppContext,
//                "#ff99cc00",
//                "2 Wash dishes"
//        ));
//
//        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
//                mAppContext,
//                "#ffffbb33",
//                "3 Complain"
//        ));
//
//        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.initColorCode(
//                mAppContext,
//                "#ffff4444",
//                "4 How long is this text box and does it really wrap around or not?"
//        ));
//    }
}
