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

package com.sadraii.hyperdex.data;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.sadraii.hyperdex.ColorCode;
import com.sadraii.hyperdex.ColorCodeDao;
import com.sadraii.hyperdex.R;

import java.util.ArrayList;

/**
 * Provides an abstraction layer between the database and controllers for manipulating Color Code
 * objects.
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

    public static long getColorCodeTag(Context context, long id) {
        return getColorCode(context, id).getTag();
    }

    /**
     * Creates and returns a {@link ColorCode} set to the next available ID. Uses
     * System.currentTimeMillis() as a unique tag for the ColorCode.
     *
     * @param context
     * @param argb
     * @param task
     * @return
     */
    public static ColorCode initColorCode(Context context, String argb, String task) {
        return new ColorCode(getNextId(context), System.currentTimeMillis(), argb, task);
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

    /**
     * Returns the ARGB values of the ColorCode in the format "#00000000".
     *
     * @param context
     * @param tag
     * @return
     */
    public static String getValueForTag(Context context, long tag) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        for (ColorCode colorCode : colorCodes) {
            if (colorCode.getTag() == tag) {
                return colorCode.getArgb();
            }
        }
        return null;
    }

    public static int getColorIntForTag(Context context, long tag) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        for (ColorCode colorCode : colorCodes) {
            if (colorCode.getTag() == tag) {
                return Color.parseColor(colorCode.getArgb());
            }
        }
        return 0;
    }

    /**
     * Returns the ColorCode with the given tag.
     *
     * @param context
     * @param tag
     * @return
     */
    public static ColorCode getColorCodeForTag(Context context, long tag) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        for (ColorCode colorCode : colorCodes) {
            if (colorCode.getTag() == tag) {
                return colorCode;
            }
        }
        return null;
    }

    /**
     * Returns the first ColorCode with the given task.
     *
     * @param context
     * @param task
     * @return
     */
    public static ColorCode getColorCodeForTask(Context context, String task) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        for (ColorCode colorCode : colorCodes) {
            if (colorCode.getTask().equals(task)) {
                return colorCode;
            }
        }
        return null;
    }

    /**
     * Updates the task of the ColorCode with the given tag, if the tasks are different.
     *
     * @param context
     * @param tag
     * @param task
     */
    public static void updateColorCodeTask(Context context, long tag, String task) {
        ColorCode colorCode = getColorCodeForTag(context, tag);
        if (colorCode != null) {
            String oldText;
            try {
                oldText = colorCode.getTask();
            } catch (NullPointerException e) {
                oldText = "";
                e.printStackTrace();
            }
            if (!oldText.equals(task)) {
                colorCode.setTask(task);
                insertOrReplace(context, colorCode);
                Log.d("CCR", "inserted text= " + task);
            }
        }
    }

    public static void insertOrReplace(Context context, ColorCode colorCode) {
        getColorCodeDao(context).insertOrReplace(colorCode);
    }

    public static void clearColorCodes(Context context) {
        getColorCodeDao(context).deleteAll();
    }

    /**
     * Deletes the ColorCode and re-sorts the remaining IDs to keep a sequential order to IDs.
     *
     * @param context
     * @param id
     */
    public static void deleteColorCodeAndSort(Context context, long id) {
        ColorCodeDao dao = getColorCodeDao(context);
        int colorCount = (int) dao.count();
        dao.delete(getColorCode(context, id));

        if (colorCount > 1) {
            for (int i = (int) id; i < colorCount - 1; i++) {
                ColorCode copyFrom = getColorCode(context, i + 1);

                insertOrReplace(context, new ColorCode(
                        copyFrom.getId() - 1,
                        copyFrom.getTag(),
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

    /**
     * Returns the ARGB values of all of the ColorCodes in the format "#00000000".
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getAllColorValues(Context context) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        ArrayList<String> colorValues = new ArrayList<>();
        for (ColorCode colorCode : colorCodes) {
            colorValues.add(colorCode.getArgb());
        }
        return colorValues;
    }

    /**
     * Returns the color-int of all of the ColorCodes according to the {@link Color} class.
     * @param context
     * @return
     */
    public static ArrayList<Integer> getAllColorInts(Context context) {
        ArrayList<String> colorValues = getAllColorValues(context);
        ArrayList<Integer> colorInts = new ArrayList<>();
        for (String color : colorValues) {
            colorInts.add(Color.parseColor(color));
        }
        return colorInts;
    }

    /**
     * Returns the task descriptions of all of the ColorCodes.
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getAllTasks(Context context) {
        ArrayList<ColorCode> colorCodes = getAllColorCodes(context);
        ArrayList<String> tasks = new ArrayList<>();
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

    public static ColorCode getColorCode(Context context, long id) {
        return getColorCodeDao(context).load(id);
    }

    public static void dropTable(Context context) {
        getColorCodeDao(context).dropTable(
                ((GreenDaoApplication) context).getDaoSession().getDatabase(),
                true);
    }

    public static void createTable(Context context) {
        getColorCodeDao(context).createTable(
                ((GreenDaoApplication) context).getDaoSession().getDatabase(),
                true);
    }

    private static ColorCodeDao getColorCodeDao(Context context) {
        return ((GreenDaoApplication) context).getDaoSession().getColorCodeDao();
    }
}