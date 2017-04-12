package com.zhang.photo.controller;

import com.zhang.photo.entity.Photo;
import com.zhang.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * Created by zhangsl on 2017/4/11.
 */
@RestController
@RequestMapping(value = "manage")
public class ManageController {

    @Value("${photo.directory}")
    private String baseDir;

    @Resource
    private PhotoService photoService;

    @GetMapping(value = "update")
    public String update(){
        photoService.truncatePhoto();
        File dir = new File(baseDir);
        File[] files = dir.listFiles();
        for (File file : files) {
            Photo photo = new Photo();
            photo.setName(file.getName());
            photo.setUpdateTime(new Date());
            photoService.save(photo);
        }
        return baseDir;
    }



}
