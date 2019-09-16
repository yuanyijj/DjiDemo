package com.dji.test.demo.util;

import com.dji.test.demo.base.MApplication;
import com.dji.test.demo.bean.WaypointBean;
import com.dji.test.demo.bean.WaypointLineBean;
import com.dji.test.demo.db.DaoSession;
import com.dji.test.demo.db.WaypointBeanDao;
import com.dji.test.demo.db.WaypointLineBeanDao;

import java.util.List;

public class GreendaoUtils {
    private static DaoSession DaoSession;

    private GreendaoUtils() {
        DaoSession = MApplication.getmDaoSession();
    }

    public static GreendaoUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final GreendaoUtils sInstance = new GreendaoUtils();
    }

    /**
     * 获取所有航线
     *
     * @return
     */
    public List<WaypointLineBean> getWaypointLinelisr() {
        return DaoSession.getWaypointLineBeanDao().queryBuilder().orderDesc(WaypointLineBeanDao.Properties.Id).build().list();
    }


    /**
     * 获取航线中的航点
     *
     * @param uId 航线id
     * @return
     */
    public List<WaypointBean> getWaypointlisr(String uId) {
        return DaoSession.getWaypointBeanDao().queryBuilder()
                .where(WaypointBeanDao.Properties.Uid.eq(uId))
                .orderAsc(WaypointBeanDao.Properties.Id).build().list();
    }


    /**
     * 新增航线
     *
     * @param info
     */
    public void saveWaypointLine(WaypointLineBean info) {
        DaoSession.getWaypointLineBeanDao().insert(info);
    }

    /**
     * 修改航线
     *
     * @param info
     */
    public void updataWaypointLine(WaypointLineBean info) {
        DaoSession.getWaypointLineBeanDao().update(info);
    }

    /**
     * 查询一条航线
     *
     * @param uId
     */
    public List<WaypointLineBean> queryWaypointLine(String  uId) {
       return DaoSession.getWaypointLineBeanDao().queryBuilder()
               .where(WaypointLineBeanDao.Properties.Id.eq(uId)).build().list();
    }



    /**
     * 新增航点
     *
     * @param info
     */
    public void saveWaypoint(WaypointBean info) {
        DaoSession.getWaypointBeanDao().insert(info);
    }

    /**
     * 修改航点
     *
     * @param info
     */
    public void updataWaypoint(WaypointBean info) {
        DaoSession.getWaypointBeanDao().update(info);
    }


    /**
     * 批量删除航点
     *
     * @param text
     */
    public void delectWaypoint(List<WaypointBean> text) {
        DaoSession.getWaypointBeanDao().deleteInTx(text);
    }

    /**
     * 删除航线
     *
     * @param text
     */
    public void delectWaypointLine(WaypointLineBean text) {
        DaoSession.getWaypointLineBeanDao().deleteInTx(text);
    }



}
