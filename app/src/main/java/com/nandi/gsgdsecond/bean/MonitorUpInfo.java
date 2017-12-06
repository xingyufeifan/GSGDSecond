package com.nandi.gsgdsecond.bean;

import java.io.Serializable;

/**
 * Created by ChenPeng on 2017/12/1.
 */

public class MonitorUpInfo implements Serializable{

    /**
     * dis_name : 测试
     * monitor_name: 墙裂
     * u_time : 2017-11-07 10:23:04
     * id : 8051783
     * monitor_id : 53631
     * has_clean : 0
     * monitor_data : 123456.0
     * is_valid : 0
     * upload_time : 2017-11-07T10:23:04
     * lon : 106.509191
     * lat : 29.608719
     * warn_level : 4
     * monitor_url : files/20171106/jcinfo/dlmeta/1510021293671.jpg
     * serial_no : null
     * warn_level1 : 4
     * adjacent_sub : 0.0
     * scheme_id : 0
     * state : 0
     */

    private String dis_name;
    private String monitor_name;
    private String u_time;
    private int id;
    private int monitor_id;
    private int has_clean;
    private double monitor_data;
    private int is_valid;
    private String upload_time;
    private double lon;
    private double lat;
    private int warn_level;
    private String monitor_url;
    private Object serial_no;
    private int warn_level1;
    private double adjacent_sub;
    private int scheme_id;
    private int state;

    public String getDis_name() {
        return dis_name;
    }

    public void setDis_name(String dis_name) {
        this.dis_name = dis_name;
    }

    public String getMonitor_name() {
        return monitor_name;
    }

    public void setMonitor_name(String monitor_name) {
        this.monitor_name = monitor_name;
    }

    public String getU_time() {
        return u_time;
    }

    public void setU_time(String u_time) {
        this.u_time = u_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonitor_id() {
        return monitor_id;
    }

    public void setMonitor_id(int monitor_id) {
        this.monitor_id = monitor_id;
    }

    public int getHas_clean() {
        return has_clean;
    }

    public void setHas_clean(int has_clean) {
        this.has_clean = has_clean;
    }

    public double getMonitor_data() {
        return monitor_data;
    }

    public void setMonitor_data(double monitor_data) {
        this.monitor_data = monitor_data;
    }

    public int getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(int is_valid) {
        this.is_valid = is_valid;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getWarn_level() {
        return warn_level;
    }

    public void setWarn_level(int warn_level) {
        this.warn_level = warn_level;
    }

    public String getMonitor_url() {
        return monitor_url;
    }

    public void setMonitor_url(String monitor_url) {
        this.monitor_url = monitor_url;
    }

    public Object getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(Object serial_no) {
        this.serial_no = serial_no;
    }

    public int getWarn_level1() {
        return warn_level1;
    }

    public void setWarn_level1(int warn_level1) {
        this.warn_level1 = warn_level1;
    }

    public double getAdjacent_sub() {
        return adjacent_sub;
    }

    public void setAdjacent_sub(double adjacent_sub) {
        this.adjacent_sub = adjacent_sub;
    }

    public int getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(int scheme_id) {
        this.scheme_id = scheme_id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {

        this.state = state;
    }

    @Override
    public String toString() {
        return "MonitorUpInfo{" +
                "dis_name='" + dis_name + '\'' +
                ", monitor_name='" + monitor_name + '\'' +
                ", u_time='" + u_time + '\'' +
                ", id=" + id +
                ", monitor_id=" + monitor_id +
                ", has_clean=" + has_clean +
                ", monitor_data=" + monitor_data +
                ", is_valid=" + is_valid +
                ", upload_time='" + upload_time + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", warn_level=" + warn_level +
                ", monitor_url='" + monitor_url + '\'' +
                ", serial_no=" + serial_no +
                ", warn_level1=" + warn_level1 +
                ", adjacent_sub=" + adjacent_sub +
                ", scheme_id=" + scheme_id +
                ", state=" + state +
                '}';
    }
}
