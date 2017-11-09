package com.nandi.gsgdsecond.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.nandi.gsgdsecond.bean.DisasterInfo;
import com.nandi.gsgdsecond.bean.DisasterPoint;
import com.nandi.gsgdsecond.bean.MonitorInfo;
import com.nandi.gsgdsecond.bean.MonitorPoint;

import com.nandi.gsgdsecond.greendao.DisasterInfoDao;
import com.nandi.gsgdsecond.greendao.DisasterPointDao;
import com.nandi.gsgdsecond.greendao.MonitorInfoDao;
import com.nandi.gsgdsecond.greendao.MonitorPointDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig disasterInfoDaoConfig;
    private final DaoConfig disasterPointDaoConfig;
    private final DaoConfig monitorInfoDaoConfig;
    private final DaoConfig monitorPointDaoConfig;

    private final DisasterInfoDao disasterInfoDao;
    private final DisasterPointDao disasterPointDao;
    private final MonitorInfoDao monitorInfoDao;
    private final MonitorPointDao monitorPointDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        disasterInfoDaoConfig = daoConfigMap.get(DisasterInfoDao.class).clone();
        disasterInfoDaoConfig.initIdentityScope(type);

        disasterPointDaoConfig = daoConfigMap.get(DisasterPointDao.class).clone();
        disasterPointDaoConfig.initIdentityScope(type);

        monitorInfoDaoConfig = daoConfigMap.get(MonitorInfoDao.class).clone();
        monitorInfoDaoConfig.initIdentityScope(type);

        monitorPointDaoConfig = daoConfigMap.get(MonitorPointDao.class).clone();
        monitorPointDaoConfig.initIdentityScope(type);

        disasterInfoDao = new DisasterInfoDao(disasterInfoDaoConfig, this);
        disasterPointDao = new DisasterPointDao(disasterPointDaoConfig, this);
        monitorInfoDao = new MonitorInfoDao(monitorInfoDaoConfig, this);
        monitorPointDao = new MonitorPointDao(monitorPointDaoConfig, this);

        registerDao(DisasterInfo.class, disasterInfoDao);
        registerDao(DisasterPoint.class, disasterPointDao);
        registerDao(MonitorInfo.class, monitorInfoDao);
        registerDao(MonitorPoint.class, monitorPointDao);
    }
    
    public void clear() {
        disasterInfoDaoConfig.clearIdentityScope();
        disasterPointDaoConfig.clearIdentityScope();
        monitorInfoDaoConfig.clearIdentityScope();
        monitorPointDaoConfig.clearIdentityScope();
    }

    public DisasterInfoDao getDisasterInfoDao() {
        return disasterInfoDao;
    }

    public DisasterPointDao getDisasterPointDao() {
        return disasterPointDao;
    }

    public MonitorInfoDao getMonitorInfoDao() {
        return monitorInfoDao;
    }

    public MonitorPointDao getMonitorPointDao() {
        return monitorPointDao;
    }

}
