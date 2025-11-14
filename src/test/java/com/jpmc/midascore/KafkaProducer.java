package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
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
        try {
            String[] transactionData = transactionLine.split(",");

            if (transactionData.length != 3) {
                System.err.println("Invalid transaction line: " + transactionLine);
                return;
            }

            long id = Long.parseLong(transactionData[0].trim());
            long userId = Long.parseLong(transactionData[1].trim());
            float amount = Float.parseFloat(transactionData[2].trim());

            Transaction tx = new Transaction(id, userId, amount);
            kafkaTemplate.send(topic, tx);

            System.out.println("Sent transaction → " + tx);

        } catch (Exception e) {
            System.err.println("Error parsing transaction line: " + transactionLine);
            e.printStackTrace();
        }
    }
}
