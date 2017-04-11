package com.zhang.photo.entity;

import java.util.Date;

/**
 * Created by zhangsl on 2017/4/11.
 */
public class Photo {

    private int id;
    private String name;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
