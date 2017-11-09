package com.nandi.gsgdsecond.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ChenPeng on 2017/10/18.
 */
@Entity
public class MonitorInfo {
    @Id
    private Long id;
    private String disasterName;
    private String monitorName;
    private String disasterNumber;
    private String monitorNumber;
    private String crackLength;
    private String happenTime;
    private String photoPath;
    private String longitude;
    private String latitude;
    @Generated(hash = 1672393113)
    public MonitorInfo(Long id, String disasterName, String monitorName,
            String disasterNumber, String monitorNumber, String crackLength,
            String happenTime, String photoPath, String longitude,
            String latitude) {
        this.id = id;
        this.disasterName = disasterName;
        this.monitorName = monitorName;
        this.disasterNumber = disasterNumber;
        this.monitorNumber = monitorNumber;
        this.crackLength = crackLength;
        this.happenTime = happenTime;
        this.photoPath = photoPath;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    @Generated(hash = 2135621837)
    public MonitorInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDisasterName() {
        return this.disasterName;
    }
    public void setDisasterName(String disasterName) {
        this.disasterName = disasterName;
    }
    public String getMonitorName() {
        return this.monitorName;
    }
    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }
    public String getDisasterNumber() {
        return this.disasterNumber;
    }
    public void setDisasterNumber(String disasterNumber) {
        this.disasterNumber = disasterNumber;
    }
    public String getMonitorNumber() {
        return this.monitorNumber;
    }
    public void setMonitorNumber(String monitorNumber) {
        this.monitorNumber = monitorNumber;
    }
    public String getCrackLength() {
        return this.crackLength;
    }
    public void setCrackLength(String crackLength) {
        this.crackLength = crackLength;
    }
    public String getHappenTime() {
        return this.happenTime;
    }
    public void setHappenTime(String happenTime) {
        this.happenTime = happenTime;
    }
    public String getPhotoPath() {
        return this.photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public String getLongitude() {
        return this.longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return this.latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "MonitorInfo{" +
                "id=" + id +
                ", disasterName='" + disasterName + '\'' +
                ", monitorName='" + monitorName + '\'' +
                ", disasterNumber='" + disasterNumber + '\'' +
                ", monitorNumber='" + monitorNumber + '\'' +
                ", crackLength='" + crackLength + '\'' +
                ", happenTime='" + happenTime + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
