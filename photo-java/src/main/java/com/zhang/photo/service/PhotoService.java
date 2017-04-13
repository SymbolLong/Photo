package com.zhang.photo.service;

import com.zhang.photo.dao.PhotoDAO;
import com.zhang.photo.dao.PhotoRepository;
import com.zhang.photo.entity.Photo;
import com.zhang.photo.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * Created by zhangsl on 2017/4/11.
 */
@Service
public class PhotoService {

    @Resource
    private PhotoRepository photoRepository;
    @Resource
    private PhotoDAO photoDAO;

    public Photo findById(int id){
        return photoRepository.findOne(id);
    }

    @Transactional
    public void save(Photo photo){
        photoRepository.save(photo);
    }

    public void truncatePhoto(){
        photoDAO.truncatePhoto();
    }

    public String updateDatabase(String baseDir){
        long start = System.currentTimeMillis();
        truncatePhoto();
        File dir = new File(baseDir);
        File[] files = dir.listFiles();
        int count = 0;
        for (File file : files) {
            boolean isPicture = CommonUtil.isPicture(file.getName());
            if (isPicture){
                Photo photo = new Photo();
                photo.setName(file.getName());
                photo.setUpdateTime(new Date());
                save(photo);
                count++;
            }
        }
        long end = System.currentTimeMillis();
        return "共处理"+count+"个文件，耗时："+(end-start)+"毫秒";
    }
}
