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

package com.msadraii.multidex;

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
import android.widget.Spinner;

import com.google.gson.Gson;
import com.msadraii.multidex.colorcode.EditColorCodeActivity;
import com.msadraii.multidex.data.ColorCodeRepository;
import com.msadraii.multidex.data.EntryRepository;
import com.msadraii.multidex.data.GreenDaoApplication;
import com.msadraii.multidex.entry.EntryFragment;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Context mAppContext;
    private HyperdexAdapter mAdapter;
    private ViewPager mPager;
    private int selectedColorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAppContext = this.getApplicationContext();
        mAdapter = new HyperdexAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.hyperdex_pager);
        mPager.setAdapter(mAdapter);
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

    public PagerAdapter getPagerAdapter() {
        return mAdapter;
    }

    public ViewPager getViewPager() {
        return mPager;
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
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO: fix getTime()
            // If scrolled to last entry, create a new one for the next day
            if (position == EntryRepository.getAllEntries(appContext).size() - 1) {
                EntryRepository.insertOrReplace(appContext,
                        EntryRepository.createEntry(appContext,
                                Calendar.getInstance().getTime(), "", 0));
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


    private void addTestEntries() {
        Calendar cal = Calendar.getInstance();
        Gson gson = new Gson();

        HashMap<Integer, Integer> entrySegments = new HashMap<>();
        entrySegments.put(72, 4);
        entrySegments.put(67, 2);
        entrySegments.put(7, 3);
        entrySegments.put(10, 1);
        Log.d("gson= ", gson.toJson(entrySegments));
        EntryRepository.insertOrReplace(mAppContext, EntryRepository.createEntry(
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
        EntryRepository.insertOrReplace(mAppContext, EntryRepository.createEntry(
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
        EntryRepository.insertOrReplace(mAppContext, EntryRepository.createEntry(
                mAppContext,
                cal.getTime(),
                gson.toJson(entrySegments),
                2
        ));
        mAdapter.notifyDataSetChanged();
    }

    private void addTestColors() {
        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.createColorCode(
                mAppContext,
                "#ff33b5e5",
                "0 Make breakfast"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.createColorCode(
                mAppContext,
                "#ffaa66cc",
                "1 Jump rope"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.createColorCode(
                mAppContext,
                "#ff99cc00",
                "2 Wash dishes"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.createColorCode(
                mAppContext,
                "#ffffbb33",
                "3 Complain"
        ));

        ColorCodeRepository.insertOrReplace(mAppContext, ColorCodeRepository.createColorCode(
                mAppContext,
                "#ffff4444",
                "4 How long is this text box and does it really wrap around or not?"
        ));
    }
}
