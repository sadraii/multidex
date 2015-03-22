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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryFragment extends Fragment {
    int fragVal;

    static EntryFragment init(int val) {
        EntryFragment truitonFrag = new EntryFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry, container,
                false);
        TextView textView = (TextView) rootView.findViewById(R.id.entry_view_date);
        textView.setText("Frag " + fragVal);
        EntryViewLayout entryViewLayout = (EntryViewLayout)rootView.findViewById(R.id.entry_view_layout);
        if (fragVal == 0) {
            entryViewLayout.setColor(Color.RED);
        } else {
            entryViewLayout.setColor(Color.LTGRAY);
        }


        return rootView;
    }
}