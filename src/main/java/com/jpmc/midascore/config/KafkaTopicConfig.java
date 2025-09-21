package com.jpmc.midascore.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfig {

    @Value("${general.kafka-topic}")  // This will get "my-midas-topic"
    private String kafkaTopicName;

    @Bean
    public NewTopic midasTopic() {
        return TopicBuilder.name(kafkaTopicName)  // Uses "my-midas-topic"
                .build();
    }
}
