package kr.go.ebankingBatch.batch;

import kr.go.ebankingBatch.entity3.OTPINFO3;
import kr.go.ebankingBatch.repository3.OtpRepository3;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.security.SecureRandom;
import java.util.Date;

@Configuration
public class OtpBatch3 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpRepository3 otpRepository;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private boolean readOnce = false;

    public OtpBatch3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, OtpRepository3 otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpRepository = otpRepository;
    }

    @Bean
    public Job otpJob3() {
        return new JobBuilder("otpJob3", jobRepository)
                .start(otpStep3())
                .build();
    }

    @Bean
    public Step otpStep3() {
        return new StepBuilder("otpStep3", jobRepository)
                .<OTPINFO3, OTPINFO3>chunk(10, platformTransactionManager)
                .reader(otpReader3())  // ItemReader에서 OTPINFO 객체를 생성
                .processor(otpProcessor3())  // 데이터 처리
                .writer(otpWriter3())  // 처리된 데이터 저장
                .build();
    }

    @Bean
    public ItemReader<OTPINFO3> otpReader3() {
        // ItemReader에서 OTPINFO 객체만 생성하여 반환합니다.
        return new ItemReader<OTPINFO3>() {
            @Override
            public OTPINFO3 read() throws Exception {

                if (readOnce) {
                    return null;  // 이미 한 번 읽었으면 null 반환
                }
                readOnce = true;
                return new OTPINFO3();  // 기본 OTPINFO 객체를 반환
            }
        };
    }

    @Bean
    public ItemProcessor<OTPINFO3, OTPINFO3> otpProcessor3() {
        return new ItemProcessor<OTPINFO3, OTPINFO3>() {
            @Override
            public OTPINFO3 process(OTPINFO3 item) throws Exception {
                // OTPINFO 객체를 받아서 값을 설정합니다.
                String secretKey = "26VOBMHKYHNB6ALGYQXHKLNFXFF64XBY";
                int serverOtp = gAuth.getTotpPassword(secretKey);
                int tmp = generateRandomSecretKey();
                // item.setOtpcode(tmp);  // OTP 코드 설정
                 item.setOtpcode(serverOtp);  // OTP 코드 설정
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
    public RepositoryItemWriter<OTPINFO3> otpWriter3() {
        // RepositoryItemWriter를 사용하여 DB에 데이터를 저장합니다.
        RepositoryItemWriter<OTPINFO3> writer = new RepositoryItemWriter<>();
        writer.setRepository(otpRepository);  // `otpRepository`를 사용하여 데이터를 저장
        writer.setMethodName("save");  // `save` 메서드로 데이터를 저장
        return writer;
    }
}
