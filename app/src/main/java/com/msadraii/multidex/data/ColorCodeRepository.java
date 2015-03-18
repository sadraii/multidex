package com.msadraii.multidex.data;

import android.content.Context;

import com.msadraii.multidex.ColorCode;
import com.msadraii.multidex.ColorCodeDao;

import java.util.ArrayList;

/**
 * Provides an abstraction layer between the database and controllers for manipulating Color Code
 * objects.
 * Created by Mostafa on 3/15/2015.
 */
public class ColorCodeRepository {

    private static final String LOG_TAG = ColorCodeRepository.class.getSimpleName();

    public static void insertOrReplace(Context context, ColorCode colorCode) {
        getColorCodeDao(context).insertOrReplace(colorCode);
    }

    public static void clearColorCodes(Context context) {
        getColorCodeDao(context).deleteAll();
    }

    /**
     * Deletes the ColorCode and re-sorts the remaining IDs to keep a sequential order to IDs.
     */
    public static void deleteColorCodeWithIdAndSort(Context context, long id) {
        ColorCode copyTo;
        int colorCount = getAllColorCodes(context).size();
        ColorCodeDao dao = getColorCodeDao(context);
        dao.delete(getColorCodeForId(context, id));

        if (colorCount > 1) {
            for (int i = (int) id; i < colorCount; i++) {
                ColorCode copyFrom = getColorCodeForId(context, i + 1);
                copyTo = new ColorCode(
                        copyFrom.getId() - 1,
                        copyFrom.getArgb(),
                        copyFrom.getTask()
                );
                insertOrReplace(context, copyTo);
                dao.delete(copyFrom);
            }
        }
    }

//    /**
//     * Deletes the ColorCode without re-sorting. Used internally by deleteColorCodeWithIdAndSort.
//     * Use deleteColorCodeWithIdAndSort to keep IDs sorted for list views.
//     */
//    public static void deleteColorCodeWithId(Context context, long id) {
//        getColorCodeDao(context).delete(getColorCodeForId(context, id));
//    }

    public static ArrayList<ColorCode> getAllColorCodes(Context context) {
        return (ArrayList) getColorCodeDao(context).loadAll();
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