package com.jpmc.midascore;

import com.jpmc.midascore.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final String topic;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaProducer(@Value("${general.kafka-topic}") String topic, KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        String[] transactionData = transactionLine.split(", ");
        Transaction transaction = new Transaction();
        transaction.setId(transactionData[0]);
        transaction.setAmount(Double.parseDouble(transactionData[1]));
        transaction.setType(transactionData[2]);
        transaction.setDescription(transactionData.length > 3 ? transactionData[3] : "");
        kafkaTemplate.send(topic, transaction);
    }
}