package com.nandi.gsgdsecond.bean;

import java.io.Serializable;

/**
 * Created by ChenPeng on 2017/12/1.
 */

public class DisasterUpInfo implements Serializable {

    /**
     * dis_name : 测试
     * u_time : 2017-11-07 10:15:41
     * id : 3444560
     * macro_id : 73164
     * macro_data : 地面新裂缝
     * macro_track : null
     * is_validate : 0
     * warn_level : 1
     * upload_time : 2017-11-07T10:15:41
     * macro_url : files/20171106/jcinfo/hgmeta/20171107101513.jpg
     * serial_no : 20171107101537623
     * scheme_id : 0
     * state : 0
     * lon : 106.509996
     * lat : 29.610456
     * otherPhenomena : 无
     * remarks : null
     */

    private String dis_name;
    private String u_time;
    private int id;
    private int macro_id;
    private String macro_data;
    private Object macro_track;
    private int is_validate;
    private int warn_level;
    private String upload_time;
    private String macro_url;
    private String serial_no;
    private int scheme_id;
    private int state;
    private double lon;
    private double lat;
    private String otherPhenomena;
    private Object remarks;

    public String getDis_name() {
        return dis_name;
    }

    public void setDis_name(String dis_name) {
        this.dis_name = dis_name;
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

    public int getMacro_id() {
        return macro_id;
    }

    public void setMacro_id(int macro_id) {
        this.macro_id = macro_id;
    }

    public String getMacro_data() {
        return macro_data;
    }

    public void setMacro_data(String macro_data) {
        this.macro_data = macro_data;
    }

    public Object getMacro_track() {
        return macro_track;
    }

    public void setMacro_track(Object macro_track) {
        this.macro_track = macro_track;
    }

    public int getIs_validate() {
        return is_validate;
    }

    public void setIs_validate(int is_validate) {
        this.is_validate = is_validate;
    }

    public int getWarn_level() {
        return warn_level;
    }

    public void setWarn_level(int warn_level) {
        this.warn_level = warn_level;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getMacro_url() {
        return macro_url;
    }

    public void setMacro_url(String macro_url) {
        this.macro_url = macro_url;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
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

    public String getOtherPhenomena() {
        return otherPhenomena;
    }

    public void setOtherPhenomena(String otherPhenomena) {
        this.otherPhenomena = otherPhenomena;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {

        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "DisasterUpInfo{" +
                "dis_name='" + dis_name + '\'' +
                ", u_time='" + u_time + '\'' +
                ", id=" + id +
                ", macro_id=" + macro_id +
                ", macro_data='" + macro_data + '\'' +
                ", macro_track=" + macro_track +
                ", is_validate=" + is_validate +
                ", warn_level=" + warn_level +
                ", upload_time='" + upload_time + '\'' +
                ", macro_url='" + macro_url + '\'' +
                ", serial_no='" + serial_no + '\'' +
                ", scheme_id=" + scheme_id +
                ", state=" + state +
                ", lon=" + lon +
                ", lat=" + lat +
                ", otherPhenomena='" + otherPhenomena + '\'' +
                ", remarks=" + remarks +
                '}';
    }

}
