package com.jpmc.midascore.consumer;

import com.jpmc.midascore.dto.TransactionDTO;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private final TransactionService transactionService;

    @Autowired
    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void consume(TransactionDTO transactionDTO) {
        System.out.println("Received transaction: " + transactionDTO.getAccountId());

        transactionService.recordTransaction(
                transactionDTO.getAccountId(),
                transactionDTO.getAmount()
        );
    }
}