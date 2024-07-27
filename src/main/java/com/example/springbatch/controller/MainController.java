package com.example.springbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;
    private final JobRegistry jobRegistry;

    public MainController(JobLauncher jobLauncher, ApplicationContext applicationContext, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.applicationContext = applicationContext;
        this.jobRegistry = jobRegistry;
    }

    @GetMapping("/first")
    public String firstApi(@RequestParam("value") String value) {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        try {
            jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }

    @GetMapping("/third")
    public String thirdApi(@RequestParam("value") String value) {

        System.out.println(value);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
//                .addLong()
                .toJobParameters();

        try {
            jobLauncher.run(jobRegistry.getJob("thirdJob"), jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }

    //https://docs.spring.io/spring-batch/reference/job/configuring-launcher.html
}
