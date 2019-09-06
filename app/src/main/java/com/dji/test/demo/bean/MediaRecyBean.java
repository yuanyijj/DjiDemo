package com.dji.test.demo.bean;


import java.util.List;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/11/29/029
 */


public class MediaRecyBean {
    //两种状态 全选和取消
    private boolean isEditStutas;
    private String index; //
    private String data;
    private String name;
    private List<MediaCheckBean> mMediaCheckBeans;

    public MediaRecyBean() {

    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



    public List<MediaCheckBean> getMediaCheckBeans() {
        return mMediaCheckBeans;
    }

    public void setMediaCheckBeans(List<MediaCheckBean> mediaCheckBeans) {
        mMediaCheckBeans = mediaCheckBeans;
    }

    public boolean isEditStutas() {
        return isEditStutas;
    }

    public void setEditStutas(boolean editStutas) {
        isEditStutas = editStutas;
    }
}
