package com.example.springbatch.repository;

import com.example.springbatch.entity.OTPINFO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface OtpRepository extends JpaRepository<OTPINFO, Long> {
}
