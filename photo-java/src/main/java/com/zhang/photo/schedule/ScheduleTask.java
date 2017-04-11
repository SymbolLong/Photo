package com.zhang.photo.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by zhangsl on 2017/4/11.
 */
@Component
public class ScheduleTask {

    @Scheduled(cron = " 0 0/30 * * * ? ")
    public void update(){
        System.out.println(new Date());
    }
}
