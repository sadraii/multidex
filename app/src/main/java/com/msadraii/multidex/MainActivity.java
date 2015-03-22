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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.msadraii.multidex.data.ColorCodeRepository;
import com.msadraii.multidex.data.EntryRepository;

public class MainActivity extends ActionBarActivity {
    private Context mAppContext;
    HyperdexAdapter mAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new HyperdexAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.hyperdex_pager);
        mPager.setAdapter(mAdapter);

        Spinner spinner = (Spinner) findViewById(R.id.color_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.test_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // TODO eraser imagebutton
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View rootView = super.onCreateView(name, context, attrs);
        mAppContext = this.getApplicationContext();

//        clearTables();
        recreateTables();
        addTestLabels();

        return rootView;
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
                // TODO settings activity
                return true;
            }
            case R.id.action_edit_label: {
                startActivity(new Intent(this, EditColorCodeActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static class HyperdexAdapter extends FragmentPagerAdapter {
        public HyperdexAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return EntryFragment.init(position);
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

    private void addTestLabels() {
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
