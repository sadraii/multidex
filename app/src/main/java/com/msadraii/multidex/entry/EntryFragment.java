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

package com.msadraii.multidex.entry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.msadraii.multidex.Entry;
import com.msadraii.multidex.R;
import com.msadraii.multidex.data.EntryRepository;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryFragment extends Fragment {
    private static final String POSITION_TAG = "position";
    private static final String DATE_DIALOGUE_FRAGMENT_TAG = "date_dialogue_fragment";

    private int mPosition;
    private Entry mEntry;

    public EntryFragment() {
    }

    public Entry getEntry() {
        return mEntry;
    }
    // TODO: may need this if entry view's mEntry has different state than fragment's mEntry
    public void setEntry(Entry entry) {
        mEntry = entry;
    }

    public static EntryFragment newInstance(int position) {
        EntryFragment entryFragment = new EntryFragment();

        Bundle args = new Bundle();
        args.putInt(POSITION_TAG, position);
        entryFragment.setArguments(args);

        return entryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = (getArguments() != null)
                ? getArguments().getInt(POSITION_TAG)
                : savedInstanceState.getInt(POSITION_TAG);

        if (mEntry == null) {
            mEntry = EntryRepository.getEntryForId(getActivity().getApplicationContext(), mPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry, container, false);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(mEntry.getDate());

        TextView textView = (TextView) rootView.findViewById(R.id.entry_view_date);
        textView.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                + " " + String.valueOf(cal.get(Calendar.DATE)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(),
                        DATE_DIALOGUE_FRAGMENT_TAG);
            }
        });

        EntryViewLayout entryViewLayout =
                (EntryViewLayout) rootView.findViewById(R.id.entry_view_layout);
        entryViewLayout.setPosition(getActivity().getApplicationContext(), mPosition);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        mPosition = (getArguments() != null)
//                ? getArguments().getInt(POSITION_TAG)
//                : savedInstanceState.getInt(POSITION_TAG);
//
//        if (mEntry == null) {
//            mEntry = EntryRepository.getEntryForId(getActivity().getApplicationContext(), mPosition);
//        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putInt(POSITION_TAG, mPosition);
        EntryRepository.insertOrReplace(getActivity().getApplicationContext(), mEntry);
        super.onSaveInstanceState(outState);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            return new DatePickerDialog(
                    getActivity(),
                    this,
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }
}