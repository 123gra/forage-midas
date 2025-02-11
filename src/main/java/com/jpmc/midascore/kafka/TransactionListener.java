package com.jpmc.midascore.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.foundation.Transaction;

@Service
public class TransactionListener {

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "midas-group")
    public void listen(ConsumerRecord<String, Transaction> record) {
        Transaction transaction = record.value();
        System.out.println("Received transaction: " + transaction);
        // No processing yet, just verifying receipt
    }
}
