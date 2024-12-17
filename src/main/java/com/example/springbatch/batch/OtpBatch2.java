package com.example.springbatch.batch;

import com.example.springbatch.entity.OTPINFO;
import com.example.springbatch.mapper.OtpMapper;
import com.example.springbatch.repository.OtpRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Configuration
public class OtpBatch2 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpMapper otpMapper;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private final OtpRepository otpRepository;

    public OtpBatch2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
                     OtpMapper otpMapper, EntityManager entityManager ,OtpRepository otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpMapper = otpMapper;
        this.otpRepository = otpRepository;
    }

    @Bean
    public Job otpJob2() {
        return new JobBuilder("otpJob2", jobRepository)
                .start(otpStep2())
                .preventRestart()
                .build();
    }

    @Bean
    public Step otpStep2() {
        return new StepBuilder("otpStep2", jobRepository)
                .<OTPINFO, OTPINFO>chunk(10, platformTransactionManager)
                .reader(otpReader2(null))  // ItemReader에서 OTPINFO 객체를 생성
                .processor(otpProcessor2())  // 데이터 처리
                .writer(otpWriter2())  // 처리된 데이터 저장
                .transactionManager(platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<OTPINFO> otpReader2(@Value("#{jobParameters['date']}") String date) {
        return new ItemReader<OTPINFO>() {
            private int count = 0;
            private String processedDate = null;

            @Override
            public OTPINFO read() throws Exception {
                if (processedDate != null && processedDate.equals(date)) {
                    return null;  // 같은 날짜이면 null 반환 (이미 처리된 데이터는 다시 읽지 않음)
                }
                processedDate = date;
                if (count < 1) {
                    OTPINFO item = new OTPINFO();
                    item.setOtpcode(generateRandomSecretKey());
                    item.setOtpdate(new Date());
                    count++;
                    return item;
                }
                return null; // 끝내기
            }
        };
    }

    @Bean
    public ItemProcessor<OTPINFO, OTPINFO> otpProcessor2() {
        return new ItemProcessor<OTPINFO, OTPINFO>() {
            @Override
            public OTPINFO process(OTPINFO item) throws Exception {
                // OTPINFO 객체를 받아서 값을 설정합니다.
//                String secretKey = "26VOBMHKYHNB6ALGYQXHKLNFXFF64XBY";
//                int serverOtp = gAuth.getTotpPassword(secretKey);
//                int tmp = generateRandomSecretKey();
//                item.setOtpcode(tmp);  // OTP 코드 설정
//                item.setOtpdate(new Date());  // 현재 날짜와 시
                return item;
            }
        };
    }

    private int generateRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(900000) + 100000;  // 6자리 숫자 (100000 - 999999)
        return randomNumber;  // 랜덤 숫자를 문자열로 반환
    }

    // MyBatis의 OtpMapper를 사용하여 데이터를 DB에 저장하는 ItemWriter 구현

    @Bean
    public ItemWriter<OTPINFO> otpWriter2() {
        return items -> {
            // 각 아이템을 출력
            for (OTPINFO item : items) {
                // 데이터 출력 (예: 콘솔에 출력)
                log.info("Generated OTP: " + item.getOtpcode() + ", OTP Date: " + item.getOtpdate());
                otpMapper.insertOtpInfo(item);  // MyBatis Mapper 사용
                //otpRepository.save(item);
               // throw new RuntimeException("1111");
            }
        };
    }
}
