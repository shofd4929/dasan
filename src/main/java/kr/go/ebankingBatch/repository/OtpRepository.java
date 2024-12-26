package kr.go.ebankingBatch.repository;

import kr.go.ebankingBatch.entity.OTPINFO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;

public interface OtpRepository extends JpaRepository<OTPINFO, Long> {
    void deleteByOtpdateBefore(Date timeBeforeTenMinutes);
}
