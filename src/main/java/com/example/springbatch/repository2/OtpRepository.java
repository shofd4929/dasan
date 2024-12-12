package com.example.springbatch.repository2;

import com.example.springbatch.entity2.OTPINFO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OTPINFO, Long> {

}
