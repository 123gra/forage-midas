package com.jpmc.midascore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration for enabling asynchronous processing
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
    // This configuration enables @Async annotation support
    // Spring will automatically create a default TaskExecutor for async operations
}
