package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.services.IncentiveService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final String topic;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final IncentiveService incentiveService;

    public KafkaProducer(@Value("${general.kafka-topic}") String topic, KafkaTemplate<String, Transaction> kafkaTemplate, IncentiveService incentiveService) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.incentiveService = incentiveService;
    }

    public void send(String transactionLine) {
        String[] transactionData = transactionLine.split(", ");
        Long senderId = Long.parseLong(transactionData[0]);
        Long recipientId = Long.parseLong(transactionData[1]);
        Float amount = Float.parseFloat(transactionData[2]);

        Float incentiveAmount = null;
        if (transactionData.length > 3) {
            incentiveAmount = Float.parseFloat(transactionData[3]);
        }
        kafkaTemplate.send(
                topic,
                new Transaction(senderId, recipientId, amount, incentiveAmount)
        );
    }
}