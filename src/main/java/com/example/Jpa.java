package com.example;

import com.example.entity.TestEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Hello world!
 *
 */
public class Jpa {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("examplePU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            TestEntity entity = new TestEntity("Rollback Test");
            em.persist(entity);

            // 의도적인 예외 발생
            if (true) {
                // throw new RuntimeException("강제 예외 → 롤백 유도");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
            em.getTransaction().rollback(); // 롤백 수행
        } finally {
            em.close();
            emf.close();
        }
    }
}