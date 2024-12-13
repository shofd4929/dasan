package com.example.springbatch.batch;

import com.example.springbatch.entity.OTPINFO;
import com.example.springbatch.mapper.OtpMapper;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Configuration
public class OtpBatch2 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpMapper otpMapper;
    private final EntityManager entityManager;  // EntityManager 주입
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private boolean readOnce = false;

    public OtpBatch2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
                     OtpMapper otpMapper, EntityManager entityManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpMapper = otpMapper;
        this.entityManager = entityManager;
    }

    @Bean
    public Job otpJob2() {
        return new JobBuilder("otpJob2", jobRepository)
                .start(otpStep2())
                .build();
    }

    @Bean
    public Step otpStep2() {
        return new StepBuilder("otpStep2", jobRepository)
                .<OTPINFO, OTPINFO>chunk(10, platformTransactionManager)
                .reader(otpReader2())  // ItemReader에서 OTPINFO 객체를 생성
                .processor(otpProcessor2())  // 데이터 처리
                .writer(otpWriter2())  // 처리된 데이터 저장
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public ItemReader<OTPINFO> otpReader2() {
        return new ItemReader<OTPINFO>() {
            @Override
            public OTPINFO read() throws Exception {
                if (readOnce) {
                    return null;  // 이미 한 번 읽었으면 null 반환
                }
                readOnce = true;
                return new OTPINFO();  // 기본 OTPINFO 객체를 반환
            }
        };
    }

    @Bean
    public ItemProcessor<OTPINFO, OTPINFO> otpProcessor2() {
        return new ItemProcessor<OTPINFO, OTPINFO>() {
            @Override
            public OTPINFO process(OTPINFO item) throws Exception {
                // OTPINFO 객체를 받아서 값을 설정합니다.
                String secretKey = "26VOBMHKYHNB6ALGYQXHKLNFXFF64XBY";
                int serverOtp = gAuth.getTotpPassword(secretKey);
                int tmp = generateRandomSecretKey();
                item.setOtpcode(tmp);  // OTP 코드 설정
                item.setOtpdate(new Date());  // 현재 날짜와 시간으로 설정
                readOnce = false;
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
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                // 트랜잭션 시작
                transaction.begin();

                // MyBatis의 Mapper를 사용하여 데이터를 DB에 저장
                for (OTPINFO item : items) {
                    otpMapper.insertOtpInfo(item);  // MyBatis Mapper 사용
                }

                // 예시로 예외를 발생시켜 롤백을 유도
                if (true) { // 예시 조건: 아이템이 5개 이상이면 예외 발생
                    throw new RuntimeException("Force rollback due to error");
                }

                // 트랜잭션 커밋
                transaction.commit();
            } catch (Exception ex) {
                // 예외 발생 시 롤백
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                // 예외를 다시 던져서 Spring Batch가 롤백을 처리하도록 함
                throw new RuntimeException("Error while processing items", ex);
            }
        };
    }
}
