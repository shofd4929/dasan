package com.example;

import com.example.config.AppConfig;
import com.example.service.TestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SDJPa {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        var service = context.getBean(TestService.class);

        try {
            service.run();
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        context.close();
    }
}
