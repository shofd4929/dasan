package kr.go.ebankingBatch.batch;

import kr.go.ebankingBatch.entity.OTPINFO;
import kr.go.ebankingBatch.repository.OtpRepository;
import kr.go.ebankingBatch.mapper.OtpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import com.warrenstrange.googleauth.GoogleAuthenticator;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Configuration
public class OtpBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpMapper otpMapper;
    private final OtpRepository otpRepository;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private boolean readOnce = false;

    public OtpBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, OtpMapper otpMapper, OtpRepository otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpMapper = otpMapper;
        this.otpRepository = otpRepository;
    }

    @Bean
    public Job otpJob() {
        return new JobBuilder("otpJob", jobRepository)
                .start(otpStep())
                .preventRestart()
                .build();
    }

    @Bean
    public Step otpStep() {
        return new StepBuilder("otpStep", jobRepository)
                .<OTPINFO, OTPINFO>chunk(10, platformTransactionManager)
                .reader(otpReader())  // ItemReader에서 OTPINFO 객체를 생성
                .processor(otpProcessor())  // 데이터 처리
                .writer(otpWriter())  // 처리된 데이터 저장
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public ItemReader<OTPINFO> otpReader() {
        // ItemReader에서 OTPINFO 객체만 생성하여 반환합니다.
        return new ItemReader<OTPINFO>() {
            private int count = 0;

            @Override
            public OTPINFO read() throws Exception {

                if (count < 1) {
                    OTPINFO item = new OTPINFO();
                    item.setId(generateRandomSecretKey());
                    item.setOtpdate(new Date());
                    count++;
                    return item;
                }
                return null; // 끝내기
            }
        };
    }

    @Bean
    public ItemProcessor<OTPINFO, OTPINFO> otpProcessor() {
        return new ItemProcessor<OTPINFO, OTPINFO>() {
            @Override
            public OTPINFO process(OTPINFO item) throws Exception {
                return item;
            }
        };
    }


    private int generateRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(900000) + 100000;  // 6자리 숫자 (100000 - 999999)
        return randomNumber;  // 랜덤 숫자를 문자열로 반환
    }

    @Bean
    public ItemWriter<OTPINFO> otpWriter() {
        // RepositoryItemWriter를 사용하여 DB에 데이터를 저장합니다.
        return items -> {
            // 각 아이템을 출력
            for (OTPINFO item : items) {
                // 데이터 출력 (예: 콘솔에 출력)
                log.info("Generated OTP: " + item.getId() + ", OTP Date: " + item.getOtpdate());
                otpMapper.insertOtpInfo(item);  // MyBatis Mapper 사용
                //otpRepository.save(item);
                // throw new RuntimeException("1111");
            }
        };
    }
}
