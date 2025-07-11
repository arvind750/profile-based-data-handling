package org.profiles.Configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

    @Component
    @EnableScheduling
    public class JobScheduler {

        @Autowired
        private JobLauncher jobLauncher;

        @Autowired
        private Job messageProcessingJob;

        @Scheduled(cron = "0 0 * * * ?") // Runs every hour
        public void runMessageProcessingJob() throws Exception {
            JobParametersBuilder paramsBuilder = new JobParametersBuilder();
            paramsBuilder.addDate("timestamp", new Date());
            jobLauncher.run(messageProcessingJob, paramsBuilder.toJobParameters());
        }
    }

