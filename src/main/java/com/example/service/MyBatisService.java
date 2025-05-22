package com.example.service;

import com.example.mapper.TestMapper;
import org.springframework.stereotype.Service;

@Service
public class MyBatisService {
    private final TestMapper testMapper;

    public MyBatisService(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    public void run() {
        int count = testMapper.countEntities();
        System.out.println("MyBatis 엔티티 수: " + count);
    }
}
