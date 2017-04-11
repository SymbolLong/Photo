package com.zhang.photo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Created by zhangsl on 2017/4/11.
 */
@RestController
@RequestMapping(value = "manage")
public class ManageController {

    @Value("${photo.directory}")
    private String baseDir;

    @GetMapping(value = "update")
    public String update(){
        File dir = new File(baseDir);
        File[] files = dir.listFiles();
        for (File file : files) {
            System.out.println(file.getName());
        }
        return baseDir;
    }

}
