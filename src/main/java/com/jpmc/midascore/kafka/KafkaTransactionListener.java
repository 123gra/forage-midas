package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaTransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionListener.class);
    private int transactionCount = 0;

    // This will listen to messages from the topic defined in application.yml
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "test-group")
    public void listen(Transaction transaction) {
        transactionCount++;
        
        logger.info("========== TRANSACTION #{} ==========", transactionCount);
        logger.info("Received transaction: {}", transaction);
        
        // Log individual transaction details for debugging
        logger.info("Transaction Details - SenderId: {}, RecipientId: {}, Amount: {}", 
                    transaction.getSenderId(), 
                    transaction.getRecipientId(), 
                    transaction.getAmount());
    }
}
