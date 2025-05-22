package com.example.service;

import com.example.entity.TestEntity;
import com.example.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    private final TestRepository repo;

    public TestService(TestRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void run() {
        TestEntity entity = new TestEntity("Hello Spring Data JPA");
        repo.save(entity);

        System.out.println("ID 확인: " + entity.getId());

        // 예외 발생
        /*
         * if (true) {
         * throw new RuntimeException("트랜잭션 롤백 테스트 예외");
         * }
         */

        var found = repo.findById(entity.getId()).orElse(null);
        System.out.println("조회된 이름: " + found.getName());
    }
}
