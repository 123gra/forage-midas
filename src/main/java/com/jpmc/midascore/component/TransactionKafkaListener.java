package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionKafkaListener.class);
    private final TransactionService transactionService;

    public TransactionKafkaListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-consumer-group")
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        // Process the transaction
        boolean processed = transactionService.processTransaction(transaction);

        if (processed) {
            logger.info("Transaction processed successfully");
        } else {
            logger.info("Transaction was invalid and discarded");
        }

        // Log Waldorf's balance for debugging
        float waldorfBalance = transactionService.getUserBalance("waldorf");
        if (waldorfBalance >= 0) {
            logger.info("WALDORF'S BALANCE: {} (rounded down: {})", waldorfBalance, (int) Math.floor(waldorfBalance));
        }

        // Log Wilbur's balance for Task 4
        float wilburBalance = transactionService.getUserBalance("wilbur");
        if (wilburBalance >= 0) {
            logger.info("WILBUR'S BALANCE: {} (rounded down: {})", wilburBalance, (int) Math.floor(wilburBalance));
        }
    }
}