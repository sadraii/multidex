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

package com.sadraii.hyperdex.entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.R;
import com.sadraii.hyperdex.data.EntryRepository;

import java.util.Calendar;
import java.util.Locale;

/**
 * TODO: description
 */
public class EntryFragment extends Fragment {

    private static final String LOG_TAG = EntryFragment.class.getSimpleName();
    private static final String POSITION_TAG = "position";

    private int mPosition;
    private Entry mEntry;
    private EntryViewLayout mEntryViewLayout;

    public EntryFragment() {
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
                : 0;

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
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String day = String.valueOf(cal.get(Calendar.DATE));

        TextView textView = (TextView) rootView.findViewById(R.id.entry_view_date);
        textView.setText(month + day);

        mEntryViewLayout = (EntryViewLayout) rootView.findViewById(R.id.entry_view_layout);
        mEntryViewLayout.setPosition(getActivity().getApplicationContext(), mPosition);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        getAndSaveEntry();
    }

    @Override
    public void onPause() {
        super.onPause();
        getAndSaveEntry();
    }

    /**
     * Retrieves the Entry from the EntryView and writes it the database.
     */
    private void getAndSaveEntry() {
        mEntry = mEntryViewLayout.getEntry();
        EntryRepository.insertOrReplace(getActivity().getApplicationContext(), mEntry);
    }
}