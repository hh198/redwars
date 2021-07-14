package com.example.redwars.config;

import com.example.redwars.service.RedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * 这个类是用做自动刷新
 */
@Configuration
@EnableScheduling
public class SaticScheduleTask {
    @Autowired
    RedService redService;
    //每分钟执行一次红包是否过期验证
    @Scheduled(cron = "0 */1 * * * ?")
    private void configureTasks() {
        redService.past();
        System.out.println("执行静态定时任务时间: " + LocalDateTime.now());
    }
}