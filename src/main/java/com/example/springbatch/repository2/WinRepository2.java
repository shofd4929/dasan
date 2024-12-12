package com.example.springbatch.repository2;

import com.example.springbatch.entity2.WinEntity2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinRepository2 extends JpaRepository<WinEntity2, Long> {

    Page<WinEntity2> findByWinGreaterThanEqual(Long win, Pageable pageable);
}
