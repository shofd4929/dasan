package com.example.springbatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
public class ThirdBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public ThirdBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Job thirdJob() {

        System.out.println("third job");

        return new JobBuilder("thirdJob", jobRepository)
                .start(thirdStep())
                .build();
    }

    @Bean
    public Step thirdStep() {

        System.out.println("third step");

        return new StepBuilder("thirdStep", jobRepository)
                .<String, String> chunk(3, platformTransactionManager)
                .reader(thirdReader())
                .processor(upperProcessor())
                .writer(thirdWriter())
                .build();
    }

    @Bean
    public ItemReader<String> thirdReader() {

        return new ListItemReader<>(Arrays.asList("kim", "lee", "park", "choi", "jeong", "ha", "jo"));
    }

    @Bean
    public ItemProcessor<String, String> upperProcessor() {

        return item -> item.toUpperCase();
    }

    @Bean
    public ItemWriter<String> thirdWriter() {

        return item -> item.forEach(System.out::println);
    }
}
