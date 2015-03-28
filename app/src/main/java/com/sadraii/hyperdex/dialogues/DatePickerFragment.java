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

package com.sadraii.hyperdex.dialogues;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.sadraii.hyperdex.MainActivity;
import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.data.EntryRepository;

import java.util.Calendar;

/**
 * Created by Mostafa on 3/26/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog.OnDateSetListener mListener;

    // TODO: copy savedintancestate stuff from ColorPicker
    // Empty constructor required for dialog fragments.
    public DatePickerFragment() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context appContext = getActivity().getApplicationContext();
        int currentItem = ((MainActivity) getActivity()).getViewPager().getCurrentItem();
        Entry entry = EntryRepository.getEntryForId(appContext, currentItem);
        Calendar cal = Calendar.getInstance();
        cal.setTime(entry.getDate());
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        long minDate = EntryRepository.getFirstEntry(appContext).getDate().getTime();
        datePickerDialog.getDatePicker().setMinDate(minDate);

        return datePickerDialog;
//        return new DatePickerDialog(
//                getActivity(),
//                this,
//                c.get(Calendar.YEAR),
//                c.get(Calendar.MONTH),
//                c.get(Calendar.DAY_OF_MONTH)
//        );
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        mListener = listener;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if (mListener != null) {
            mListener.onDateSet(view, year, month, day);
        }

        // TODO: handle default ondateset behavior

        dismiss();
    }
}