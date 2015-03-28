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

package com.sadraii.hyperdex.data;

import android.content.Context;

import com.sadraii.hyperdex.ColorCode;
import com.sadraii.hyperdex.ColorCodeDao;
import com.sadraii.hyperdex.R;

import java.util.ArrayList;

/**
 * Provides an abstraction layer between the database and controllers for manipulating Color Code
 * objects.
 * Created by Mostafa on 3/15/2015.
 */
public class ColorCodeRepository {

    private static final String LOG_TAG = ColorCodeRepository.class.getSimpleName();

    /**
     * Creates, inserts, and returns the next available {@link ColorCode}.
     *
     * @param context
     * @param argb
     * @param task
     * @return
     */
    public static ColorCode addNextColorCode(Context context, String argb, String task) {
        ColorCode colorCode = initColorCode(context, argb, task);
        insertOrReplace(context, colorCode);
        return colorCode;
    }

    /**
     * Creates and returns a {@link ColorCode} set to the next available ID.
     *
     * @param context
     * @param argb
     * @param task
     * @return
     */
    public static ColorCode initColorCode(Context context, String argb, String task) {
        return new ColorCode(getNextId(context), argb, task);
    }

    /**
     * Returns the next unused ID.
     *
     * @param context
     * @return
     */
    public static long getNextId(Context context) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        return (colorCodes == null) ? 0 : colorCodes.size();
    }

    public static void insertOrReplace(Context context, ColorCode colorCode) {
        getColorCodeDao(context).insertOrReplace(colorCode);
    }

    public static void clearColorCodes(Context context) {
        getColorCodeDao(context).deleteAll();
    }

    /**
     * Deletes the ColorCode and re-sorts the remaining IDs to keep a sequential order to IDs.
     */
    public static void deleteColorCodeAndSort(Context context, long id) {
        int colorCount = getAllColorCodes(context).size();
        ColorCodeDao dao = getColorCodeDao(context);
        dao.delete(getColorCodeForId(context, id));

        if (colorCount > 1) {
            for (int i = (int) id; i < colorCount - 1; i++) {
                ColorCode copyFrom = getColorCodeForId(context, i + 1);
                insertOrReplace(context, new ColorCode(
                        copyFrom.getId() - 1,
                        copyFrom.getArgb(),
                        copyFrom.getTask()
                ));
                dao.delete(copyFrom);
            }
        }
    }

    public static int size(Context context) {
        return (int) getColorCodeDao(context).count();
    }

    public static ArrayList<ColorCode> getAllColorCodes(Context context) {
        return (ArrayList<ColorCode>) getColorCodeDao(context).loadAll();
    }

    public static ArrayList<String> getAllColorValues(Context context) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        ArrayList<String> colorValues = new ArrayList<String>();
        for (ColorCode colorCode : colorCodes) {
            colorValues.add(colorCode.getArgb());
        }
        return colorValues;
    }

    public static ArrayList<String> getAllTasks(Context context) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        ArrayList<String> tasks = new ArrayList<String>();
        for (ColorCode colorCode : colorCodes) {
            String task = colorCode.getTask();
            if (task == null || task.equals("")) {
                tasks.add(context.getString(R.string.hint_list_item_description));
            } else {
                tasks.add(colorCode.getTask());
            }
        }
        return tasks;
    }

    public static ColorCode getColorCodeForId(Context context, long id) {
        return getColorCodeDao(context).load(id);
    }

    public static void dropTable(Context c) {
        getColorCodeDao(c).dropTable(
                ((GreenDaoApplication) c.getApplicationContext()).getDaoSession().getDatabase(),
                true);
    }

    public static void createTable(Context c) {
        getColorCodeDao(c).createTable(
                ((GreenDaoApplication) c.getApplicationContext()).getDaoSession().getDatabase(),
                true);
    }

    private static ColorCodeDao getColorCodeDao(Context c) {
        return ((GreenDaoApplication) c.getApplicationContext()).getDaoSession().getColorCodeDao();
    }
}