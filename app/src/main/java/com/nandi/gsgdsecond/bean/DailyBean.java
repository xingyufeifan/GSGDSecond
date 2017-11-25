package com.nandi.gsgdsecond.bean;

import java.io.Serializable;

/**
 * Created by qingsong on 2017/11/25.
 */

public class DailyBean implements Serializable {

    /**
     * id : 2292
     * user_name : 杨峰春
     * record_time : 2017-11-02T09:44:24
     * units : 高峰镇
     * jobContent : 讯后核查。
     * user_id : 21
     */

    private int id;
    private String user_name;
    private String record_time;
    private String units;
    private String jobContent;
    private Object memberName;
    private String user_id;

    public DailyBean(int id, String user_name, String record_time, String units, String jobContent, Object memberName, String user_id) {
        this.id = id;
        this.user_name = user_name;
        this.record_time = record_time;
        this.units = units;
        this.jobContent = jobContent;
        this.memberName = memberName;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getJobContent() {
        return jobContent;
    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }

    public Object getMemberName() {
        return memberName;
    }

    public void setMemberName(Object memberName) {
        this.memberName = memberName;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
