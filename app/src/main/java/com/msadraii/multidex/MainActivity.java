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
    private Context mAppContext;
    private HyperdexAdapter mAdapter;
    private ViewPager mPager; // TODO: convert to local?
    private Spinner mSpinner; // TODO: check how buttons are done in other apps
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
        recreateTables();
        addTestColors();
        addTestEntries();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                ColorCodeRepository.getAllTasks(mAppContext));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner = (Spinner) findViewById(R.id.color_spinner);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
//        private Context appContext = GreenDaoApplication.getAppContext();
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public HyperdexAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return EntryRepository.getAllEntries(GreenDaoApplication.getAppContext()).size();
        }

        @Override
        public Fragment getItem(int position) {
            return EntryFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
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
//        entrySegments.addColorCodeId(4);
//        entrySegments.addColorCodeId(2);
//        entrySegments.addColorCodeId(3);
//        entrySegments.addColorCodeId(1);
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
//        entrySegments.addColorCodeId(1);
//        entrySegments.addColorCodeId(0);
//        entrySegments.addColorCodeId(4);
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
//        entrySegments.addColorCodeId(2);
//        entrySegments.addColorCodeId(3);
//        entrySegments.addColorCodeId(1);
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
//
//        label = new ColorCode();
//        label.setArgb("#ff0099cc");
//        label.setTask("6 Go hiking");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff9933cc");
//        label.setTask("7 Homework LOL");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff669900");
//        label.setTask("8 Watch TBBT");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffff8800");
//        label.setTask("9 Sleep");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffcc0000");
//        label.setTask("10 Fix this app!");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff292884");
//        label.setTask("11 Be lazy");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffdd3a94");
//        label.setTask("12 How long is this text box and does it really wrap around or not?");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffcccccc");
//        label.setTask("13 Visit Hawaii");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff888888");
//        label.setTask("14 Get more sleep");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff33b5e5");
//        label.setTask("15 Mmmm lollipops");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffaa66cc");
//        label.setTask("16 Almost there");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff292884");
//        label.setTask("17 Almost there");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff99cc00");
//        label.setTask("18 Need more items");
//        ColorCodeRepository.insertOrReplace(mAppContext, label);
    }
}
