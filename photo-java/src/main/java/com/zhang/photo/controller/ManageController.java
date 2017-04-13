package com.zhang.photo.controller;

import com.zhang.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
        return photoService.updateDatabase(baseDir);
    }



}
