package com.msadraii.multidex;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class GreenDaoApplication extends Application {

    public DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
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

}