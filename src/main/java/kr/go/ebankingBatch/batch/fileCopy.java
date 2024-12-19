package kr.go.ebankingBatch.batch;

import kr.go.ebankingBatch.entity.OTPINFO;
import kr.go.ebankingBatch.repository.OtpRepository;
import kr.go.ebankingBatch.mapper.OtpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Slf4j
@Configuration
public class fileCopy {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpMapper otpMapper;
    private final OtpRepository otpRepository;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public fileCopy(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, OtpMapper otpMapper, OtpRepository otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpMapper = otpMapper;
        this.otpRepository = otpRepository;
    }

    @Bean
    public Job fileCopyJob() {
        return new JobBuilder("fileCopyJob", jobRepository)
                .start(fileCoopydStep())
                .listener(new JobLoggerListener())
                .build();
    }

    @Bean
    public Step fileCoopydStep() {
        return new StepBuilder("fileCoopydStep", jobRepository)
                .tasklet(fileCoopydTasklet(), platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet fileCoopydTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Hello, World!");
            return RepeatStatus.FINISHED;
        };
    }

}
