package com.jpmc.midascore.kafka;

import com.jpmc.midascore.kafka.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    // ⚠️ New: Use a static counter to track transactions across messages
    private static int transactionCount = 0;

    // ⚠️ New: Use a static StringBuilder to collect the first four amounts
    private static final StringBuilder amountsBuilder = new StringBuilder();

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {

        // 1. Increment the counter for each message received
        transactionCount++;

        // 2. Only record the amount for the first four transactions
        if (transactionCount <= 4) {
            amountsBuilder.append(transaction.getAmount());
            logger.info("Transaction #{} captured. Current collection: {}",
                    transactionCount, amountsBuilder.toString());
        }

        // 3. Once the 4th transaction is captured, print the final result
        if (transactionCount == 4) {
            logger.info("-------------------------------------------------------");
            logger.info("✅ TASK 2 RESULT (First 4 Amounts): {}", amountsBuilder.toString());
            logger.info("-------------------------------------------------------");
        }
    }
}