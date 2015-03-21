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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO settings activity
            return true;
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
}
