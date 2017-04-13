package com.zhang.photo.controller;

import com.zhang.photo.entity.Photo;
import com.zhang.photo.service.PhotoService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by zhangsl on 2017/4/13.
 */
@Controller
@RequestMapping(value = "api")
public class ApiController {

    @Value("${photo.directory}")
    private String baseDir;

    @Resource
    private PhotoService photoService;

    @GetMapping(value = "get")
    public void findPhoto(HttpServletResponse response,int id){
        try {
            Photo photo = photoService.findById(id);
            if (photo == null){
                response.setHeader("Content-type", "text/json;charset=UTF-8");
                response.getWriter().write("资源不存在或已被删除");
                return;
            }
            File file = new File(baseDir+"/"+photo.getName());
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = IOUtils.toByteArray(inputStream);
            OutputStream stream = response.getOutputStream();
            stream.write(data);
            stream.flush();
            stream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
