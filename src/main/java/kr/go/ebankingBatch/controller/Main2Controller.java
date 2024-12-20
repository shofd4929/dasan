package kr.go.ebankingBatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Controller
@ResponseBody
public class Main2Controller {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final ThreadPoolTaskScheduler taskScheduler; // 스케줄러 관리
    private ScheduledFuture<?> scheduledFuture; // 스케줄된 작업을 관리할 변수

    public Main2Controller(JobLauncher jobLauncher, JobRegistry jobRegistry, ThreadPoolTaskScheduler taskScheduler) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
        this.taskScheduler = taskScheduler;
    }

    // 배치 작업 시작 API
    @GetMapping("/startOtpJob")
    public String startOtpJob(@RequestParam("value") String value) throws Exception {
        // 배치 작업 실행
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        // 배치 작업 실행
        jobLauncher.run(jobRegistry.getJob("otpJob"), jobParameters);

        return "OTP 배치 작업이 시작되었습니다.";
    }

    // 배치 작업 종료 API (스케줄 중지)
    @GetMapping("/stopOtpJob")
    public String stopOtpJob() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);  // 실행 중인 작업을 취소합니다.
            return "OTP 배치 작업이 중지되었습니다.";
        }
        return "OTP 배치 작업이 이미 중지되었습니다.";
    }

    // 주기적으로 실행되는 OTP 배치 작업 (기존 스케줄 작업)
    @GetMapping("/scheduleOtpJob")
    public String scheduleOtpJob() {
        // 스케줄 작업 실행
        scheduledFuture = taskScheduler.schedule(this::runOtpJob, new Date(System.currentTimeMillis())); // 새로 스케줄을 시작
        return "OTP 배치 작업이 새로 시작되었습니다.";
    }

    // OTP 배치 작업 실행
    public void runOtpJob() {
        try {
            System.out.println("otp schedule start");

            String value = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", value)
                    .toJobParameters();

            jobLauncher.run(jobRegistry.getJob("otpJob"), jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
