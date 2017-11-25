package com.nandi.gsgdsecond.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nandi.gsgdsecond.bean.DailyLogInfo;
import com.nandi.gsgdsecond.bean.DisasterInfo;
import com.nandi.gsgdsecond.bean.DisasterPoint;
import com.nandi.gsgdsecond.bean.MonitorInfo;
import com.nandi.gsgdsecond.bean.MonitorPoint;

import java.util.List;

/**
 * Created by ChenPeng on 2017/10/11.
 */

public class GreenDaoHelper {
    private static DaoSession daoSession;

    /**
     * 初始化greenDao
     * 建议放在Application 中进行
     */

    public static void initDatabase(Context context){
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "gsgd_db", null);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    public static void insertDisaster(DisasterPoint disasterPoint){
        daoSession.getDisasterPointDao().insertOrReplace(disasterPoint);
    }
    public static DisasterPoint queryDisaster(String number){
        return daoSession.getDisasterPointDao().queryBuilder().where(DisasterPointDao.Properties.Number.eq(number)).unique();
    }
    public static List<DisasterPoint> queryDisasterList(){
        return daoSession.getDisasterPointDao().queryBuilder().list();
    }
    public static void insertMonitor(MonitorPoint monitorPoint){
        daoSession.getMonitorPointDao().insertOrReplace(monitorPoint);
    }
    public static MonitorPoint queryMonitor(String disNumber,String monNumber){
        return daoSession.getMonitorPointDao().queryBuilder().where(MonitorPointDao.Properties.DisasterNumber.eq(disNumber),MonitorPointDao.Properties.MonitorNumber.eq(monNumber)).unique();
    }
    public static List<MonitorPoint> queryMonitorList(){
        return daoSession.getMonitorPointDao().queryBuilder().list();
    }
    public static List<MonitorPoint> queryMonitorListByNumber(String number){
        return daoSession.getMonitorPointDao().queryBuilder().where(MonitorPointDao.Properties.DisasterNumber.eq(number)).list();
    }
    public static void deleteAll(){
        daoSession.getMonitorPointDao().deleteAll();
        daoSession.getDisasterPointDao().deleteAll();
    }
    public static void insertDisasterInfo(DisasterInfo disasterInfo){
        daoSession.getDisasterInfoDao().insertOrReplace(disasterInfo);
    }
    public static void deleteDisasterInfo(List<DisasterInfo> disasterInfo){
        daoSession.getDisasterInfoDao().deleteInTx(disasterInfo);
    }
    public static List<DisasterInfo> queryDisasterInfoByNumber(String number){
        return daoSession.getDisasterInfoDao().queryBuilder().where(DisasterInfoDao.Properties.Number.eq(number)).list();
    }
    public static void insertMonitorInfo(MonitorInfo monitorInfo){
        daoSession.getMonitorInfoDao().insertOrReplace(monitorInfo);
    }
    public static void deleteMonitorInfo(MonitorInfo monitorInfo){
        daoSession.getMonitorInfoDao().delete(monitorInfo);
    }
    public static void updateMonitorInfo(MonitorInfo monitorInfo){
        daoSession.getMonitorInfoDao().update(monitorInfo);
    }
    public static MonitorInfo queryMonitorInfoByNumber(String disNo,String MonNo){
        return daoSession.getMonitorInfoDao().queryBuilder().where(MonitorInfoDao.Properties.DisasterNumber.eq(disNo)
        ,MonitorInfoDao.Properties.MonitorNumber.eq(MonNo)).unique();
    }

    public static void insertDailyLogInfo(DailyLogInfo dailyLogInfo){
        daoSession.getDailyLogInfoDao().insertOrReplace(dailyLogInfo);
    }
    public static void deleteDailyLogInfo(DailyLogInfo dailyLogInfo){
        daoSession.getDailyLogInfoDao().delete(dailyLogInfo);
    }
    public  static  void  deleteOneDailyLog(Long id){
        daoSession.getDailyLogInfoDao().deleteByKey(id);
    }
    public static void updateDailyLogInfo(DailyLogInfo dailyLogInfo){
        daoSession.getDailyLogInfoDao().update(dailyLogInfo);
    }
    public static List<DailyLogInfo> queryDailyLogInfoList(){
        return daoSession.getDailyLogInfoDao().queryBuilder().list();
    }
    //根据记录时间查询单条日志
    public static DailyLogInfo queryDailyLogInfo(String time){
        return daoSession.getDailyLogInfoDao().queryBuilder()
                .where(DailyLogInfoDao.Properties.Remarks.eq(time)).unique();
    }

    public static void deleteAllInfo(){
        daoSession.getDisasterInfoDao().deleteAll();
        daoSession.getMonitorInfoDao().deleteAll();
        daoSession.getDailyLogInfoDao().deleteAll();
    }
}
