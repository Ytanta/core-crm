package org.example.scheduler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class SecondJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("SecondJob started at: " + java.time.LocalTime.now());
        try {
            Thread.sleep(30000); // имитация долгой работы
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("SecondJob finished at: " + java.time.LocalTime.now());
    }
}