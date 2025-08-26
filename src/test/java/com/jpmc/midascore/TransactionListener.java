package com.jpmc.midascore;

// Task 3

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionListener {

    private final TransactionProcessor transactionProcessor;

    public TransactionListener(TransactionProcessor transactionProcessor) {
        this.transactionProcessor = transactionProcessor;
    }

    // Consume Transaction objects from Kafka
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        try {
            String[] parts = message.split(",");
            long senderId = Long.parseLong(parts[0].trim());
            long recipientId = Long.parseLong(parts[1].trim());
            BigDecimal amount = new BigDecimal(parts[2].trim());
            Transaction transaction = new Transaction(senderId, recipientId, amount);
            transactionProcessor.process(transaction);
        } catch (Exception e) {
            System.err.println("Failed to process message: " + message);
            e.printStackTrace();
        }
    }
}
