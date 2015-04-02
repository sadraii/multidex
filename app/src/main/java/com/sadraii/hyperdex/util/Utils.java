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

package com.sadraii.hyperdex.util;

import android.content.Context;

import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.data.EntryRepository;

import java.util.Calendar;
import java.util.Date;

/**
 * TODO: editText
 */
public class Utils {

    public static String toHexString(int color) {
        return "#" + Integer.toHexString(color);
    }

    /**
     * Returns the ID of the {@link Entry} for today.
     *
     * @param   context
     * @return  The ID of the {@link Entry} for today.
     */
    public static int getEntryIdForToday(Context context) {
        int id = 0;
        if (EntryRepository.size(context) != 0) {
            Calendar cal = Calendar.getInstance();

            id = daysSinceFirstEntry(context,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE));
        }
        return id;
    }

    // TODO: test 2 times for same day. test different days, but plus/minus 24 hours on the current day. should not add 1 day if more than 24hrs, go by DATE not TIME
    /**
     * Returns the number of days since the date of the first {@link Entry}.
     *
     * @param   context
     * @param   year
     * @param   month
     * @param   day
     * @return  The number of days since the date of the first {@link Entry}.
     */
    @SuppressWarnings("deprecation")
    public static int daysSinceFirstEntry(Context context, int year, int month, int day) {
        Entry firstEntry = EntryRepository.getFirstEntry(context);
        Calendar cal = Calendar.getInstance();

        cal.setTime(firstEntry.getDate());
        int firstDay = cal.get(Calendar.DAY_OF_YEAR);
        int firstYear = cal.get(Calendar.YEAR);

        // Date() counts year 1900 as 0
        cal.setTime(new Date(year - 1900, month, day));
        int currentDay = cal.get(Calendar.DAY_OF_YEAR);
        int currentYear = cal.get(Calendar.YEAR);

        int differenceInDays = 0;

        // If both dates are in the same year, simply subtract days.
        // Otherwise, loop through the years adding full years and partial days for the last year.
        if (firstYear == currentYear) {
            differenceInDays = currentDay - firstDay;
        } else {
            for (int i = currentYear; i > firstYear; i--) {
                // Account for leap year
                int daysInYear = (i % 4 == 0) ? 366 : 365;

                // If on the last year, add partial days. Otherwise add full year.
                if (firstYear == i - 1) {
                    differenceInDays += daysInYear - firstDay + currentDay;
                } else {
                    differenceInDays += daysInYear;
                }
            }
        }
        return differenceInDays;
    }
}
