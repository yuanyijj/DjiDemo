package com.dji.test.demo.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.dji.test.demo.db.DaoMaster;
import com.dji.test.demo.db.DaoSession;
import com.secneo.sdk.Helper;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MApplication extends Application {

    public static final String DB_NAME = "app.db";
    public static Handler mFpvHandler;
    private static DaoSession mDaoSession;

    private static BaseProduct product= null;
    private static Context mContext;
    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
        MultiDex.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        mFpvHandler=new Handler(Looper.getMainLooper());
        initGreenDao();
    }

    public static Context getContext() {
        return mContext;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) {
            return null;
        }
        return (Aircraft) getProductInstance();
    }

    public static boolean isAircraftConnected() {
            return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static synchronized BaseProduct getProductInstance() {

        product = DJISDKManager.getInstance().getProduct();
        return product;
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getmDaoSession() {
        return mDaoSession;
    }

}
