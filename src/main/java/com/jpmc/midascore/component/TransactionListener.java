package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    @Autowired
    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "forage-midas", groupId = "midas-group")
    public void listen(Transaction txn) {
    	System.out.println("Received transaction: " + txn);
        transactionService.handleTransaction(txn);
    }
}