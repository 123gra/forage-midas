package com.jpmc.midascore;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.component.TransactionService;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class KafkaConsumer {

    private final TransactionService transactionService;

    public KafkaConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
        topics = "${general.kafka-topic}",
        groupId = "midas-core-group"
    )
    public void listen(Transaction transaction) {
        // Process the transaction (validate and record to database if valid)
        boolean processed = transactionService.processTransaction(transaction);
        
        if (processed) {
            System.out.println("✅ Transaction processed - Amount: " + transaction.getAmount());
        } else {
            System.out.println("❌ Transaction discarded - Amount: " + transaction.getAmount());
        }
    }
}