package com.jpmc.midascore;

import com.jpmc.midascore.component.TransactionService;
import com.jpmc.midascore.foundation.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final TransactionService transactionService;

    @Autowired
    public KafkaConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "forage-midas", groupId = "midas-group")
    public void listen(Transaction txn) {
        transactionService.handleTransaction(txn);
    }
}
