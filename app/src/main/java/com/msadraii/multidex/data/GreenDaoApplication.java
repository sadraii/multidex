package com.msadraii.multidex.data;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.msadraii.multidex.DaoMaster;
import com.msadraii.multidex.DaoSession;

/**
 * Initializes and provides a global context for GreenDao.
 * Created by Mostafa on 3/15/2015.
 */
public class GreenDaoApplication extends Application {

    public DaoSession mDaoSession;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setupDatabase();
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "multidex_db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static Context getAppContext() {
        return context;
    }

}