package kr.go.ebaningBatch.mapper;

import kr.go.ebankingBatch.entity.OTPINFO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
public interface OtpMapper {

    void insertOtpInfo(OTPINFO otpInfo); // XML에서 정의한 insert 쿼리를 호출

    OTPINFO findOtpInfoByCode(int otpcode); // XML에서 정의한 select 쿼리를 호출

    List<OTPINFO> findAllOtpInfo(); // XML에서 정의한 select 쿼리를 호출
}
