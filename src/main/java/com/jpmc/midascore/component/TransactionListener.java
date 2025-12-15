package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionListener {

    private final TransactionHandler transactionHandler;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);
        transactionHandler.processRequestedTransaction(transaction);
    }
}
