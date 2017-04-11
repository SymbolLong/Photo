package com.zhang.photo.service;

import com.zhang.photo.dao.PhotoDAO;
import com.zhang.photo.dao.PhotoRepository;
import com.zhang.photo.entity.Photo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by zhangsl on 2017/4/11.
 */
@Service
public class PhotoService {

    @Resource
    private PhotoRepository photoRepository;
    @Resource
    private PhotoDAO photoDAO;

    @Transactional
    public void save(Photo photo){
        photoRepository.save(photo);
    }

    public void truncatePhoto(){
        photoDAO.truncatePhoto();
    }
}
