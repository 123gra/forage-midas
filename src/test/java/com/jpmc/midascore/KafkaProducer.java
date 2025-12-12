package com.jpmc.midascore;

import com.jpmc.midascore.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class KafkaProducer {
    private final String topic;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaProducer(@Value("${kafka.topic.transactions}") String topic, KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        String[] transactionData = transactionLine.split(", ");
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                transactionData[1],
                new BigDecimal(transactionData[2]),
                Instant.now().toString(),
                transactionData[0]
        );
        kafkaTemplate.send(topic, transaction);
    }
}
