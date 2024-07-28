package com.example.springbatch.repository;

import com.example.springbatch.entity.WinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WinRepository extends JpaRepository<WinEntity, Long> {

    Optional<WinEntity> findByWinGreaterThanEqual(Long win);
}
