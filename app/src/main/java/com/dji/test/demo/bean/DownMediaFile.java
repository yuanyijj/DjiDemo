package com.dji.test.demo.bean;

import dji.sdk.media.MediaFile;

/**
 * $activityName
 *  选中的实体类
 * @author LiuTao
 * @date 2018/11/10/010
 */


public class DownMediaFile {
    private int fristId;
    private int secondId;
    private MediaFile mMediaFile;

    public DownMediaFile(int fristId, int secondId, MediaFile mediaFile) {
        this.fristId = fristId;
        this.secondId = secondId;
        this.mMediaFile = mediaFile;
    }

    public int getFristId() {
        return fristId;
    }

    public void setFristId(int fristId) {
        this.fristId = fristId;
    }

    public int getSecondId() {
        return secondId;
    }

    public void setSecondId(int secondId) {
        this.secondId = secondId;
    }

    public MediaFile getMediaFile() {
        return mMediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        mMediaFile = mediaFile;
    }
}
