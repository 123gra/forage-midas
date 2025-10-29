package com.jpmc.midascore;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public NewTopic testTopic() {
        return new NewTopic("test-topic", 1, (short) 1);
    }
}