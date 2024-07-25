package com.example.springbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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

    public MainController(JobLauncher jobLauncher, ApplicationContext applicationContext) {
        this.jobLauncher = jobLauncher;
        this.applicationContext = applicationContext;
    }

    @GetMapping("/first")
    public String mainApi(@RequestParam("value") String value) {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        Job jobFirst = applicationContext.getBean("firstJob", Job.class);

        try {
            jobLauncher.run(jobFirst, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }

    //https://docs.spring.io/spring-batch/reference/job/configuring-launcher.html
}
