package com.zhang.photo.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhangsl on 2017/4/11.
 */
@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    @Column(name = "update_time")
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
