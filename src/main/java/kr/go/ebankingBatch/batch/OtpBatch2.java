package kr.go.ebankingBatch.batch;

import kr.go.ebankingBatch.entity2.OTPINFO2;
import kr.go.ebankingBatch.repository2.OtpRepository2;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
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

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Configuration
public class OtpBatch2 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpRepository2 otpRepository;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private boolean readOnce = false;

    public OtpBatch2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, OtpRepository2 otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpRepository = otpRepository;
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
                .<OTPINFO2, OTPINFO2>chunk(10, platformTransactionManager)
                .reader(otpReader2())  // ItemReader에서 OTPINFO 객체를 생성
                .processor(otpProcessor2())  // 데이터 처리
                .writer(otpWriter2())  // 처리된 데이터 저장
                .build();
    }

    @Bean
    public ItemReader<OTPINFO2> otpReader2() {
        // ItemReader에서 OTPINFO 객체만 생성하여 반환합니다.
        return new ItemReader<OTPINFO2>() {
            @Override
            public OTPINFO2 read() throws Exception {

                if (readOnce) {
                    return null;  // 이미 한 번 읽었으면 null 반환
                }
                readOnce = true;
                return new OTPINFO2();  // 기본 OTPINFO 객체를 반환
            }
        };
    }

    @Bean
    public ItemProcessor<OTPINFO2, OTPINFO2> otpProcessor2() {
        return new ItemProcessor<OTPINFO2, OTPINFO2>() {
            @Override
            public OTPINFO2 process(OTPINFO2 item) throws Exception {
                // OTPINFO 객체를 받아서 값을 설정합니다.
                String secretKey = "26VOBMHKYHNB6ALGYQXHKLNFXFF64XBY";
                int serverOtp = gAuth.getTotpPassword(secretKey);
                int tmp = generateRandomSecretKey();
                item.setOtpcode(tmp);  // OTP 코드 설정
                // item.setOtpcode(serverOtp);  // OTP 코드 설정
                item.setOtpdate(new Date());  // 현재 날짜와 시간으로 설정
                readOnce = false;
                return item;  // 처리된 OTPINFO 객체 반환
            }
        };
    }


    private int generateRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(900000) + 100000;  // 6자리 숫자 (100000 - 999999)
        return randomNumber;  // 랜덤 숫자를 문자열로 반환
    }


    @Bean
    public ItemWriter<OTPINFO2> otpWriter2() {
        return items -> {
            // 각 아이템을 출력
            for (OTPINFO2 item : items) {
                // 데이터 출력 (예: 콘솔에 출력)
                log.info("Generated OTP: " + item.getOtpcode() + ", OTP Date: " + item.getOtpdate());
                //otpMapper.insertOtpInfo(item);  // MyBatis Mapper 사용
                otpRepository.save(item);
                 throw new RuntimeException("1111");
            }
        };
    }
    /*
    @Bean
    public RepositoryItemWriter<OTPINFO2> otpWriter2() {
        // RepositoryItemWriter를 사용하여 DB에 데이터를 저장합니다.
        RepositoryItemWriter<OTPINFO2> writer = new RepositoryItemWriter<>();
        writer.setRepository(otpRepository);  // `otpRepository`를 사용하여 데이터를 저장
        writer.setMethodName("save");  // `save` 메서드로 데이터를 저장
        return writer;
    }*/
}
