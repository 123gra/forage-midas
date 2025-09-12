package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(MyKafkaListener.class);

    private final TransactionService transactionService;

    public MyKafkaListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
        topics = "${general.kafka-topic}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTransaction(Transaction tx) {
        log.info("📩 Received transaction: {}", tx);

        boolean success = transactionService.processTransaction(tx);

        if (success) {
            log.info("✅ Transaction processed successfully: {}", tx);
        } else {
            log.warn("❌ Invalid transaction discarded: {}", tx);
        }
    }
}
