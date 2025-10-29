package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(@Value("${general.kafka-topic}") String topic, KafkaTemplate<String, String> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, transactionLine);
        try {
            future.get(10, TimeUnit.SECONDS); // Wait for send completion with timeout
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Error sending message to Kafka: {}", e.getMessage());
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}