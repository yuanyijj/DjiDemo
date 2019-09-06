package com.dji.test.demo.bean;

import java.io.Serializable;

import dji.sdk.media.MediaFile;

/**
 * $activityName
 *
 * @author ${LiuTao}
 * @date 2018/5/17/017
 */

public class MediaCheckBean implements Serializable {
    boolean isCheck =false;
    MediaFile mMediaFile =null;
    boolean isDownLoaded =false;
    int  pointId;

    public MediaCheckBean() {
    }

    public MediaCheckBean(boolean isCheck, boolean isDownLoaded, MediaFile mediaFile, int pointId) {
        this.isCheck = isCheck;
        this.isDownLoaded = isDownLoaded;
        this.mMediaFile = mediaFile;
        this.pointId = pointId;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public boolean isDownLoaded() {
        return isDownLoaded;
    }

    public void setDownLoaded(boolean downLoaded) {
        isDownLoaded = downLoaded;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public MediaFile getMediaFile() {
        return mMediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        mMediaFile = mediaFile;
    }
}
