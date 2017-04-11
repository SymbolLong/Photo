package com.zhang.photo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by zhangsl on 2017/4/11.
 */
@Repository
public class PhotoDAO {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void truncatePhoto() {
        String sql = "TRUNCATE photo";
        jdbcTemplate.execute(sql);
    }
}
