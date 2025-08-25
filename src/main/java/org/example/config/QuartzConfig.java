package org.example.config;

import org.example.scheduler.OwnerContactJob;
import org.example.scheduler.SecondJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail ownerContactJobDetail() {
        return JobBuilder.newJob(OwnerContactJob.class)
                .withIdentity("ownerContactJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ownerContactTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(ownerContactJobDetail())
                .withIdentity("ownerContactTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * ? * *")) // каждую минуту
                .build();
    }

    @Bean
    public JobDetail secondJobDetail() {
        return JobBuilder.newJob(SecondJob.class)
                .withIdentity("secondJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger secondJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(secondJobDetail())
                .withIdentity("secondJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("30 * * ? * *")) // каждая минута на 30-й секунде
                .build();
    }
}