package com.zhang.photo.dao;

import com.zhang.photo.entity.Photo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zhangsl on 2017/4/11.
 */
public interface PhotoRepository extends CrudRepository<Photo,Integer> {

}
