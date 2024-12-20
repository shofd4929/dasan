package kr.go.ebankingBatch.batch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import kr.go.ebankingBatch.mapper.OtpMapper;
import kr.go.ebankingBatch.repository.OtpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

@Slf4j
@Configuration
public class fileCopy {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final OtpMapper otpMapper;
    private final OtpRepository otpRepository;


    public fileCopy(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, OtpMapper otpMapper, OtpRepository otpRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.otpMapper = otpMapper;
        this.otpRepository = otpRepository;
    }

    @Bean
    public Job fileCopyJob() {
        return new JobBuilder("fileCopyJob", jobRepository)
                .start(fileCopyStep())
                .listener(new JobLoggerListener())
                .build();
    }

    @Bean
    public Step fileCopyStep() {
        return new StepBuilder("fileCopyStep", jobRepository)
                .tasklet(fileCopyTasklet(), platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet fileCopyTasklet() {
        return (contribution, chunkContext) -> {

            log.info("fileCopy start");

            String host = "192.168.243.21";
            String user = "onenet";
            String password = "Bbwjsqnr1!B";
            String localDirectory = "C:/down/down1/"; // 로컬 디렉토리
            String remoteDirectory = "/home/onenet/down/"; // 원격 디렉토리
            String remoteArchiveDirectory = "/home/onenet/archived/"; // 원격 파일 이동할 디렉토리

            JSch jsch = new JSch();
            Session session = null;

            try {
                // 세션 설정
                session = jsch.getSession(user, host, 2222);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no"); // 호스트 키 체크 설정
                session.connect();

                // SFTP 채널 열기
                ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();

                // 원격 디렉토리의 모든 파일 읽기
                Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(remoteDirectory);

                for (ChannelSftp.LsEntry entry : fileList) {
                    if (!entry.getAttrs().isDir() && entry.getFilename().contains("특정문자열")) {
                        String remoteFilePath = remoteDirectory + entry.getFilename();
                        String localFilePath = localDirectory + entry.getFilename();

                        // 파일 다운로드
                        try (FileOutputStream outputStream = new FileOutputStream(new File(localFilePath))) {
                            channelSftp.get(remoteFilePath, outputStream);
                            log.info("Downloaded: " + entry.getFilename());
                        } catch (Exception e) {
                            log.error("Failed to download: " + entry.getFilename(), e);
                            continue; // 다운로드 실패 시 다음 파일로 진행
                        }

                        // 원격 파일 이동
                        String remoteArchivedPath = remoteArchiveDirectory + entry.getFilename();

                        try {
                            channelSftp.rename(remoteFilePath, remoteArchivedPath);
                            log.info("Moved: " + entry.getFilename() + " to " + remoteArchiveDirectory);
                        } catch (Exception e) {
                            log.error("Failed to move: " + entry.getFilename(), e);
                        }
                    }
                }

                // SFTP 채널 종료
                channelSftp.disconnect();
            } catch (Exception e) {
                log.error("Error during file processing", e);
            } finally {
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            }

            return RepeatStatus.FINISHED;
        };
    }
}