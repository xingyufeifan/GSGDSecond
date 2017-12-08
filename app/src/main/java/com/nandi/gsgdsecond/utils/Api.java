package com.nandi.gsgdsecond.utils;

import android.content.Context;

/**
 * Created by ChenPeng on 2017/10/11.
 */

public class Api {
    private String ip,port;
    private String ports;

    public Api(Context context) {
        ip= (String) SharedUtils.getShare(context,Constant.IP,"183.230.108.112");
        port= (String) SharedUtils.getShare(context,Constant.PORT,"8099");
        ports = "8090";
    }
    public String getLoginUrl(){
        return "http://"+ip+":"+port+"/meteor/findFunCfg.do";
    }
    public String getMacoUrl(){
        return "http://"+ip+":"+port+"/meteor/findMacro.do";
    }
    public String getMonitorUrl(){
        return "http://"+ip+":"+port+"/meteor/findMonitor.do";
    }
    public String getUploadUrl() {
        return "http://" + ip + ":" + port + "/meteor/saveMonDate.do";
    }

    //   日志上报
    public String getLogReportUrl() {
        return "http://" + ip + ":" + ports + "/meteor/DailyApp/saveWorkLog.do";
    }

    //   周报上报
    public String getWeeklyUrl() {
        return "http://" +  ip + ":" + ports+"/meteor/DailyApp/saveWeekLog.do";
    }

    //   灾情速报文本上传
    public String getDisReportTextUrl(){
        return "http://" + ip + ":" + ports+"/meteor/DailyApp/saveDisater.do";
    }

    //   灾情速报图片上传
    public String getDisReportPicUrl(){
        return "http://" + ip + ":" + ports+"/meteor/DailyApp/disPic.do";
    }

}
