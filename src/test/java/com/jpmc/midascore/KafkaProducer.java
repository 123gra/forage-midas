package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final String topic;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaProducer(@Value("${general.kafka-topic}") String topic,
                         KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    /** The tests call this with a CSV line: "fromUserId, toUserId, amount" */
    public void send(String transactionLine) {
        String[] parts = transactionLine.split(",\\s*");
        long from = Long.parseLong(parts[0]);
        long to = Long.parseLong(parts[1]);
        float amount = Float.parseFloat(parts[2]);

        kafkaTemplate.send(topic, new Transaction(from, to, amount));
    }
}
