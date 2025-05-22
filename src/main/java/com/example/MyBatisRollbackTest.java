package com.example;

import com.example.config.AppConfig;
import com.example.config.MyBatisConfig;
import com.example.service.MyBatisRollService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyBatisRollbackTest {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class, MyBatisConfig.class);
        var service = context.getBean(MyBatisRollService.class);

        try {
            service.runWithRollback(); // 예외 발생 → 롤백됨
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        service.printCount(); // rollback 되었으면 insert 결과 없음

        context.close();
    }
}
