package com.msadraii.multidex.data;

import android.content.Context;

import com.msadraii.multidex.Label;
import com.msadraii.multidex.LabelDao;

import java.util.ArrayList;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class LabelRepository {

    public static void insertOrReplace(Context context, Label label) {
        getLabelDao(context).insertOrReplace(label);
    }

    public static void clearLabels(Context context) {
        getLabelDao(context).deleteAll();
    }

    public static void deleteLabelWithId(Context context, long id) {
        getLabelDao(context).delete(getLabelForId(context, id));
    }

    public static ArrayList<Label> getAllLabels(Context context) {
        return (ArrayList) getLabelDao(context).loadAll();
    }

    public static Label getLabelForId(Context context, long id) {
        return getLabelDao(context).load(id);
    }

    public static void dropTable(Context c) {
        getLabelDao(c).dropTable(
                ((GreenDaoApplication) c.getApplicationContext()).getDaoSession().getDatabase(),
                true);
    }

    public static void createTable(Context c) {
        getLabelDao(c).createTable(
                ((GreenDaoApplication) c.getApplicationContext()).getDaoSession().getDatabase(),
                true);
    }

    private static LabelDao getLabelDao(Context c) {
        return ((GreenDaoApplication) c.getApplicationContext()).getDaoSession().getLabelDao();
    }
}