package com.nandi.gsgdsecond.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by baohongyan on 2017/11/22.
 */

@Entity
public class DailyLogInfo implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    @Id
    private Long id;
    private String time;
    private String userName;
    private String workType;
    private String situation;
    private String logContent;
    private String remarks;

    @Generated(hash = 670239200)
    public DailyLogInfo(Long id, String time, String userName, String workType,
            String situation, String logContent, String remarks) {
        this.id = id;
        this.time = time;
        this.userName = userName;
        this.workType = workType;
        this.situation = situation;
        this.logContent = logContent;
        this.remarks = remarks;
    }

    @Generated(hash = 108100862)
    public DailyLogInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getWorkType() {
        return this.workType;
    }
    public void setWorkType(String workType) {
        this.workType = workType;
    }
    public String getSituation() {
        return this.situation;
    }
    public void setSituation(String situation) {
        this.situation = situation;
    }
    public String getLogContent() {
        return this.logContent;
    }
    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }
    public String getRemarks() {
        return this.remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "DailyLogInfo{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", userName='" + userName + '\'' +
                ", workType='" + workType + '\'' +
                ", situation='" + situation + '\'' +
                ", logContent='" + logContent + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
