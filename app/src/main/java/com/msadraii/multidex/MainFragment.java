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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msadraii.multidex.data.ColorCodeRepository;
import com.msadraii.multidex.data.EntryRepository;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private Context appContext;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        appContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.text);

//        clearTables();
        recreateTables();
        addTestLabels();





        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_label) {
            startActivity(new Intent(getActivity(), EditColorCodeActivity.class));
            return true;
        }
        return true;
    }

    private void recreateTables() {
        ColorCodeRepository.dropTable(appContext);
        EntryRepository.dropTable(appContext);
        ColorCodeRepository.createTable(appContext);
        EntryRepository.createTable(appContext);
    }

    private void clearTables() {
        ColorCodeRepository.clearColorCodes(appContext);
        EntryRepository.clearEntries(appContext);
    }

    private void addTestLabels() {
        ColorCodeRepository.insertOrReplace(appContext, ColorCodeRepository.createColorCode(
                appContext,
                "#ff33b5e5",
                "0 Make breakfast"
        ));

        ColorCodeRepository.insertOrReplace(appContext, ColorCodeRepository.createColorCode(
                appContext,
                "#ffaa66cc",
                "1 Jump rope"
        ));

        ColorCodeRepository.insertOrReplace(appContext, ColorCodeRepository.createColorCode(
                appContext,
                "#ff99cc00",
                "2 Wash dishes"
        ));


        ColorCodeRepository.insertOrReplace(appContext, ColorCodeRepository.createColorCode(
                appContext,
                "#ffffbb33",
                "3 Complain"
        ));


        ColorCodeRepository.insertOrReplace(appContext, ColorCodeRepository.createColorCode(
                appContext,
                "#ffff4444",
                "4 How long is this text box and does it really wrap around or not?"
        ));
//
//        label = new ColorCode();
//        label.setArgb("#ff0099cc");
//        label.setTask("6 Go hiking");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff9933cc");
//        label.setTask("7 Homework LOL");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff669900");
//        label.setTask("8 Watch TBBT");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffff8800");
//        label.setTask("9 Sleep");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffcc0000");
//        label.setTask("10 Fix this app!");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff292884");
//        label.setTask("11 Be lazy");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffdd3a94");
//        label.setTask("12 How long is this text box and does it really wrap around or not?");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffcccccc");
//        label.setTask("13 Visit Hawaii");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff888888");
//        label.setTask("14 Get more sleep");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff33b5e5");
//        label.setTask("15 Mmmm lollipops");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ffaa66cc");
//        label.setTask("16 Almost there");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff292884");
//        label.setTask("17 Almost there");
//        ColorCodeRepository.insertOrReplace(appContext, label);
//
//        label = new ColorCode();
//        label.setArgb("#ff99cc00");
//        label.setTask("18 Need more items");
//        ColorCodeRepository.insertOrReplace(appContext, label);
    }
}
