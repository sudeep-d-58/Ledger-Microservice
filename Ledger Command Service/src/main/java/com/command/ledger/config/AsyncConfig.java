package com.command.ledger.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public TaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setThreadNamePrefix("AsyncThread-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
