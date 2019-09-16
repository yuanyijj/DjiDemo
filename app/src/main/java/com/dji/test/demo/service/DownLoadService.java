package com.dji.test.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dji.test.demo.FPV2Activity;
import com.dji.test.demo.bean.WaypointLineBean;
import com.dji.test.demo.mediadown.down.DownLoadDeleteHelper;
import com.dji.test.demo.util.GreendaoUtils;

import java.util.ArrayList;
import java.util.List;

import dji.sdk.media.MediaFile;

public class DownLoadService extends Service {


    private DownLoadDeleteHelper mDownLoadHelper;
    private List<MediaFile> mMediaFiles = new ArrayList<>();
private String WaypointLineNum;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Serializable
        mMediaFiles = intent.getParcelableExtra("MediaFiles");
        WaypointLineNum=intent.getStringExtra("LineNum");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
