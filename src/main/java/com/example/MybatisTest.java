package com.example;

import com.example.config.AppConfig;
import com.example.config.MyBatisConfig;
import com.example.service.MyBatisService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MybatisTest {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class, MyBatisConfig.class);
        var service = context.getBean(MyBatisService.class);
        service.run();
        context.close();
    }
}