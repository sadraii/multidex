package com.msadraii.multidex.data;

import android.content.Context;

import com.msadraii.multidex.ColorCode;
import com.msadraii.multidex.ColorCodeDao;

import java.util.ArrayList;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class ColorCodeRepository {

    public static void insertOrReplace(Context context, ColorCode colorCode) {
        getColorCodeDao(context).insertOrReplace(colorCode);
    }

    public static void clearColorCodes(Context context) {
        getColorCodeDao(context).deleteAll();
    }

    public static void deleteColorCodeWithId(Context context, long id) {
        getColorCodeDao(context).delete(getColorCodeForId(context, id));
    }

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