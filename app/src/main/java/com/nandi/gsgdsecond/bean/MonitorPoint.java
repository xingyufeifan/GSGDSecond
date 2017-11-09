package com.nandi.gsgdsecond.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by ChenPeng on 2017/10/11.
 */
@Entity
public class MonitorPoint implements Serializable{
    private static final long serialVersionUID = -8060210544600464481L;
    @Id
    private Long id;
    private String name;
    private String disasterNumber;
    private String monitorNumber;
    private String type;
    private String dimension;
    private String monitorType;
    @Generated(hash = 1052726057)
    public MonitorPoint(Long id, String name, String disasterNumber,
            String monitorNumber, String type, String dimension,
            String monitorType) {
        this.id = id;
        this.name = name;
        this.disasterNumber = disasterNumber;
        this.monitorNumber = monitorNumber;
        this.type = type;
        this.dimension = dimension;
        this.monitorType = monitorType;
    }
    @Generated(hash = 32220235)
    public MonitorPoint() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "MonitorPoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", disasterNumber='" + disasterNumber + '\'' +
                ", monitorNumber='" + monitorNumber + '\'' +
                '}';
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDimension() {
        return this.dimension;
    }
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }
    public String getMonitorType() {
        return this.monitorType;
    }
    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }
}
