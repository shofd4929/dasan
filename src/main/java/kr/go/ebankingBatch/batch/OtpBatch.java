package kr.go.ebankingBatch.batch;

import kr.go.ebankingBatch.entity.OTPINFO;
import kr.go.ebankingBatch.repository.OtpRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import com.warrenstrange.googleauth.GoogleAuthenticator;

import java.security.SecureRandom;
import java.util.Date;

@Configuration
public class OtpBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpRepository otpRepository;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();


    public OtpBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, OtpRepository otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpRepository = otpRepository;
    }

    @Bean
    public Job otpJob() {
        return new JobBuilder("otpJob", jobRepository)
                .start(otpStep())
                .build();
    }

    @Bean
    public Step otpStep() {
        return new StepBuilder("otpStep", jobRepository)
                .<OTPINFO, OTPINFO>chunk(10, platformTransactionManager)
                .reader(otpReader(null))  // ItemReader에서 OTPINFO 객체를 생성
                .processor(otpProcessor())  // 데이터 처리
                .writer(otpWriter())  // 처리된 데이터 저장
                .build();
    }

    @Bean
    public ItemReader<OTPINFO> otpReader(@Value("#{jobParameters['date']}") String date) {
        // ItemReader에서 OTPINFO 객체만 생성하여 반환합니다.
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
                    item.setId(generateRandomSecretKey());
                    item.setOtpdate(new Date());
                    count++;
                    return item;
                }
                return null; // 끝내
            }
        };
    }

    @Bean
    public ItemProcessor<OTPINFO, OTPINFO> otpProcessor() {
        return new ItemProcessor<OTPINFO, OTPINFO>() {
            @Override
            public OTPINFO process(OTPINFO item) throws Exception {
                // OTPINFO 객체를 받아서 값을 설정합니다.
                String secretKey = "26VOBMHKYHNB6ALGYQXHKLNFXFF64XBY";
                int serverOtp = gAuth.getTotpPassword(secretKey);
                int tmp = generateRandomSecretKey();
                // item.setOtpcode(tmp);  // OTP 코드 설정
                item.setId(serverOtp);  // OTP 코드 설정
                item.setOtpdate(new Date());  // 현재 날짜와 시간으로 설정

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
    public RepositoryItemWriter<OTPINFO> otpWriter() {
        // RepositoryItemWriter를 사용하여 DB에 데이터를 저장합니다.
        RepositoryItemWriter<OTPINFO> writer = new RepositoryItemWriter<>();
        writer.setRepository(otpRepository);  // `otpRepository`를 사용하여 데이터를 저장
        writer.setMethodName("save");  // `save` 메서드로 데이터를 저장
        return writer;
    }
}