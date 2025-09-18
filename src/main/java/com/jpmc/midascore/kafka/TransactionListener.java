package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "transactions", groupId = "midas-core")
    public void listen(Transaction tx) {
        transactionService.processTransaction(tx);
    }
}
