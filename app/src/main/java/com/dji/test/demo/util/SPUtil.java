package com.dji.test.demo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.dji.test.demo.base.MApplication;


/**
 * Created by 蒋 on 2018/4/23.
 * 单例
 * SP工具类
 */

public class SPUtil {
    private SharedPreferences share;
    private SharedPreferences.Editor editor;
    private String SHARED_NAME = "fiSP";//sp的文件名

    private SPUtil() {
        share = MApplication.getContext().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        editor = share.edit();
    }

    public static SPUtil getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final SPUtil sInstance = new SPUtil();
    }

    /**
     * ------- Int ---------
     */
    public void putInt(String spName, int value) {
        editor.putInt(spName, value);
        editor.commit();
    }

    public int getInt(String spName, int defaultvalue) {
        return share.getInt(spName, defaultvalue);
    }


    /**
     * ------- String ---------
     */
    public void putString(String spName, String value) {
        editor.putString(spName, value);
        editor.commit();
    }

    public String getString(String spName, String defaultvalue) {
        return share.getString(spName, defaultvalue);
    }

    public String getString(String spName) {
        return share.getString(spName, "");
    }


    /**
     * ------- boolean ---------
     */
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return share.getBoolean(key, defValue);
    }

    /**
     * ------- float ---------
     */
    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String key, float defValue) {
        return share.getFloat(key, defValue);
    }


    /**
     * ------- long---------
     */
    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, long defValue) {
        return share.getLong(key, defValue);
    }


    /**
     * 清空SP里所有数据
     */
    public void clear() {
        editor.clear();//清空
        editor.commit();//提交
    }

    /**
     * 删除SP里指定key对应的数据项
     *
     * @param key
     */
    public void remove(String key) {
        editor.remove(key);//删除掉指定的值
        editor.commit();//提交
    }

    /**
     * 查看sp文件里面是存在 key
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return share.contains(key);
    }
}
