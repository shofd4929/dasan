package com.example.springbatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SecondBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public SecondBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Job secondJob() {

        return new JobBuilder("secondJob", jobRepository)
                .start()
                .build();
    }

    @Bean
    public Step secondStep() {

        return new StepBuilder("secondStep", jobRepository)
                .build();
    }
}
