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

package com.sadraii.hyperdex.util;

import android.content.Context;

import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.data.EntryRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mostafa on 3/19/2015.
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
        Entry entry = EntryRepository.getFirstEntry(context);
        if (entry != null) {
            Date first = entry.getDate();
            Date today = Calendar.getInstance().getTime();
            long difference = today.getTime() - first.getTime();
            return (int) TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        }
        return 0;
    }

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

        // Date() counts 1900 as 0 for year
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

//    public static int safeLongToInt(long l) {
//        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
//            throw new IllegalArgumentException
//                    (l + " cannot be cast to int without changing its value.");
//        }
//        return (int) l;
//    }
}
