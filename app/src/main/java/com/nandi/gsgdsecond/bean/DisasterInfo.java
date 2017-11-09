package com.nandi.gsgdsecond.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ChenPeng on 2017/10/16.
 */
@Entity
public class DisasterInfo {
    @Id
    private Long id;
    private String name;
    private boolean find;
    private String photoPath;
    private String number;
    @Generated(hash = 1119735781)
    public DisasterInfo(Long id, String name, boolean find, String photoPath,
            String number) {
        this.id = id;
        this.name = name;
        this.find = find;
        this.photoPath = photoPath;
        this.number = number;
    }
    @Generated(hash = 1267394946)
    public DisasterInfo() {
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
    public boolean getFind() {
        return this.find;
    }
    public void setFind(boolean find) {
        this.find = find;
    }
    public String getPhotoPath() {
        return this.photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "DisasterInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", find=" + find +
                ", photoPath='" + photoPath + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
