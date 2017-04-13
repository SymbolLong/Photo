package com.zhang.photo.schedule;

import com.zhang.photo.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zhangsl on 2017/4/11.
 */
@Component
public class ScheduleTask {

    private Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Value("${photo.directory}")
    private String baseDir;
    @Resource
    private PhotoService photoService;


    @Scheduled(cron = " 0 0 0/1 * * ? ")
    public void update() {
        String result = photoService.updateDatabase(baseDir);
        logger.info(result);
    }
}
