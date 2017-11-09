package com.nandi.gsgdsecond.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by ChenPeng on 2017/10/11.
 */
@Entity
public class DisasterPoint implements Serializable{
    private static final long serialVersionUID = -7060210544600464481L;
    @Id
    private Long id;
    private String name;
    private String number;
    private String disasterType;
    private String type;
    private String longitude;
    private String latitude;
    @Generated(hash = 1037673631)
    public DisasterPoint(Long id, String name, String number, String disasterType,
            String type, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.disasterType = disasterType;
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    @Generated(hash = 410069605)
    public DisasterPoint() {
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
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getDisasterType() {
        return this.disasterType;
    }
    public void setDisasterType(String disasterType) {
        this.disasterType = disasterType;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
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
}
