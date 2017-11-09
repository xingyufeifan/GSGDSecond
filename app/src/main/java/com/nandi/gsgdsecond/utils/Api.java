package com.nandi.gsgdsecond.utils;

import android.content.Context;

/**
 * Created by ChenPeng on 2017/10/11.
 */

public class Api {
    private String ip,port;

    public Api(Context context) {
        ip= (String) SharedUtils.getShare(context,Constant.IP,"183.230.108.112");
        port= (String) SharedUtils.getShare(context,Constant.PORT,"8099");
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

}
