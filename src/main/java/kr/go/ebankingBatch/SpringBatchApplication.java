package kr.go.ebankingBatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBatchApplication {

    // 오라클 브랜치
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }
}
