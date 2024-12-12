package com.example.springbatch.batch;

import com.example.springbatch.entity2.AfterEntity2;
import com.example.springbatch.entity2.BeforeEntity2;
import com.example.springbatch.entity2.OTPINFO;
import com.example.springbatch.repository2.OtpRepository;
import com.example.springbatch.repository2.BeforeRepository2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import com.warrenstrange.googleauth.GoogleAuthenticator;

import java.util.Date;
import java.util.Map;

@Configuration
public class OtpBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final BeforeRepository2 beforeRepository;
    private final OtpRepository OtpRepository;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public OtpBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, BeforeRepository2 beforeRepository, OtpRepository OtpRepository) {

        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.beforeRepository = beforeRepository;
        this.OtpRepository = OtpRepository;
    }

    @Bean
    public Job otpJob() {

        System.out.println("otp job");

        return new JobBuilder("otpJob", jobRepository)
                //.incrementer(new RunIdIncrementer())
                .start(otpStep())
                .build();
    }

    @Bean
    public Step otpStep() {

        System.out.println("otp step");

        return new StepBuilder("otpStep", jobRepository)
                .<BeforeEntity2, OTPINFO> chunk(10, platformTransactionManager)
                .reader(otpReader())
                .processor(otpProcessor())
                .writer(otpWriter())
                .build();
    }

    //https://github.com/spring-projects/spring-batch/blob/main/spring-batch-samples/src/main/java/org/springframework/batch/samples/jpa/JpaRepositoryJobConfiguration.java
    @Bean
    public RepositoryItemReader<BeforeEntity2> otpReader() {

        return new RepositoryItemReaderBuilder<BeforeEntity2>()
                .name("otpReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<BeforeEntity2, OTPINFO> otpProcessor() {

        return new ItemProcessor<BeforeEntity2, OTPINFO>() {

            @Override
            public OTPINFO process(BeforeEntity2 item) throws Exception {

                OTPINFO otpinfo = new OTPINFO();
                String secretKey = "26VOBMHKYHNB6ALGYQXHKLNFXFF64XBY";
                int serverOtp = gAuth.getTotpPassword(secretKey);
                otpinfo.setOtpcode((long)serverOtp);
                Date currentDate = new Date();
                otpinfo.setOtpdate(currentDate);

                return otpinfo;
            }
        };
    }

    @Bean
    public RepositoryItemWriter<OTPINFO> otpWriter() {

        return new RepositoryItemWriterBuilder<OTPINFO>()
                .repository(OtpRepository)
                .methodName("save")
                .build();
    }
}
