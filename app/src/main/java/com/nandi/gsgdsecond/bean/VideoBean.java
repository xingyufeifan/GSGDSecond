package com.nandi.gsgdsecond.bean;

/**
 * Created by ChenPeng on 2017/11/25.
 */

public class VideoBean {
    private String path;
    private boolean check;
    private String  name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
