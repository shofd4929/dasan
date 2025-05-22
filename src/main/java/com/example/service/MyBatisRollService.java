package com.example.service;

import com.example.entity.TestEntity;
import com.example.mapper.TestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyBatisRollService {

    private final TestMapper testMapper;

    public MyBatisRollService(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    @Transactional
    public void runWithRollback() {
        TestEntity entity = new TestEntity("MyBatis Insert");
        testMapper.insertTest(entity);
        System.out.println("삽입된 ID: " + entity.getId());

        // 강제로 예외 발생 → 롤백 테스트
        throw new RuntimeException("트랜잭션 롤백 유도 예외");
    }

    public void printCount() {
        int count = testMapper.countAll();
        System.out.println("현재 엔티티 수: " + count);
    }
}
