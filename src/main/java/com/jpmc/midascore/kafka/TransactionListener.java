package com.jpmc.midascore.kafka;

import com.jpmc.midascore.model.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListener {

    @KafkaListener(topics = "${kafka.topic.transactions}", groupId = "midas-core")
    public void handleTransaction(Transaction transaction) {
        // For now, just log or print out what you received
        System.out.println("Received transaction: " + transaction);
    }
}
