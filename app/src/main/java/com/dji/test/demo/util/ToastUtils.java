package com.dji.test.demo.util;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.dji.test.demo.base.MApplication;


/**
 * * @author ${LiuTao}
 *
 * @date 2018/3/17/017
 */

public class ToastUtils {
    private static Toast toast;
    private static Handler mUIHandler = new Handler(Looper.getMainLooper());

    public static void showToast(final String msg) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(MApplication.getContext(), msg + "", Toast.LENGTH_SHORT);
                toast.setText(msg + "");
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public static void showToast(final int resId) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(MApplication.getContext(), resId, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setText(resId);
                toast.show();
            }
        });
    }

    public static void showToast(final int resId, boolean append) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(MApplication.getContext(), resId + "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setText(resId);
                toast.show();
            }
        });
    }
}
