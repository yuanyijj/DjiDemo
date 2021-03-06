package com.dji.test.demo.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dji.test.demo.bean.WaypointBean;
import com.dji.test.demo.bean.WaypointLineBean;

import com.dji.test.demo.db.WaypointBeanDao;
import com.dji.test.demo.db.WaypointLineBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig waypointBeanDaoConfig;
    private final DaoConfig waypointLineBeanDaoConfig;

    private final WaypointBeanDao waypointBeanDao;
    private final WaypointLineBeanDao waypointLineBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        waypointBeanDaoConfig = daoConfigMap.get(WaypointBeanDao.class).clone();
        waypointBeanDaoConfig.initIdentityScope(type);

        waypointLineBeanDaoConfig = daoConfigMap.get(WaypointLineBeanDao.class).clone();
        waypointLineBeanDaoConfig.initIdentityScope(type);

        waypointBeanDao = new WaypointBeanDao(waypointBeanDaoConfig, this);
        waypointLineBeanDao = new WaypointLineBeanDao(waypointLineBeanDaoConfig, this);

        registerDao(WaypointBean.class, waypointBeanDao);
        registerDao(WaypointLineBean.class, waypointLineBeanDao);
    }
    
    public void clear() {
        waypointBeanDaoConfig.clearIdentityScope();
        waypointLineBeanDaoConfig.clearIdentityScope();
    }

    public WaypointBeanDao getWaypointBeanDao() {
        return waypointBeanDao;
    }

    public WaypointLineBeanDao getWaypointLineBeanDao() {
        return waypointLineBeanDao;
    }

}
