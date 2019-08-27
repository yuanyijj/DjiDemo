package com.dji.test.demo.util;

import android.view.View;

import com.dji.test.demo.base.MApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

public class ViewClickHookUtils {
    public static String TAG = "ViewClickHookUtils";

    public static void HookViewClick(View view) {
        try {
            Class viewClass = Class.forName("android.view.View");//加载
            Method listenerInfoMethod = viewClass.getDeclaredMethod("getListenerInfo");//获取到这个方法 拿到View的ListenerInfo类

            if (!listenerInfoMethod.isAccessible()) {//标志对像是否可以访问 如果不可以访问
                listenerInfoMethod.setAccessible(true);//强制设置可访问标志
                //。经过测试，在大量的反射情况下， 设置Accessible为true可以提升性能20倍以上。
            }
            Object listenerInfoObj = listenerInfoMethod.invoke(view);

            Class listenerInfoClazz = Class.forName("android.view.View$ListenerInfo");
            Field onClickListenerField = listenerInfoClazz.getDeclaredField("mOnClickListener");//拿到私有属性
            //修改修饰符带来不能访问的问题
            if (!onClickListenerField.isAccessible()) {
                onClickListenerField.setAccessible(true);
            }
            View.OnClickListener mOnClickListener = (View.OnClickListener) onClickListenerField.get(listenerInfoObj);
            //自定义事件监听器
            View.OnClickListener onClickListenerProxy = new OnClickListenerProxy(mOnClickListener);
            //更换成自己的点击事件
            onClickListenerField.set(listenerInfoObj, onClickListenerProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //自定义的事件监听器
    private static class OnClickListenerProxy implements View.OnClickListener {
        private View.OnClickListener object;
        //点击时间控制
        private int MIN_CLICK_DELAY_TIME = 1500;

        private long lastClickTime = 0;

        private OnClickListenerProxy(View.OnClickListener object) {
            this.object = object;
        }

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                if (object != null) {
                    //判断飞机是否可用
                    if (MApplication.getAircraftInstance() == null) {
                        LogUtil.v(TAG,"未连接");
                        return;
                    }
                    object.onClick(v);
                }
            }
        }
    }
}
