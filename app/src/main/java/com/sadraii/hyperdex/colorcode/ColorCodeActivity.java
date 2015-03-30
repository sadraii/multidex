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

package com.sadraii.hyperdex.colorcode;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sadraii.hyperdex.R;


public class ColorCodeActivity extends ActionBarActivity {

//    private static float elevation;
//    private static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color_codes);

        if (savedInstanceState == null) {
            ColorCodeFragment fragment = ColorCodeFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_color_code_container, fragment)
                    .commit();
        }

//        actionBar = getSupportActionBar();
//        elevation = actionBar.getElevation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_color_codes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public static void setActionBarShadow(boolean showShadow) {
//        if (actionBar != null) {
//            if (showShadow) {
//                actionBar.setElevation(elevation);
//            } else {
//                actionBar.setElevation(0f);
//            }
//        }
//    }
}
