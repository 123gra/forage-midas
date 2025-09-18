package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionProcessingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaTransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionListener.class);
    private final TransactionProcessingService transactionProcessingService;
    private int transactionCount = 0;
    private int validTransactionCount = 0;
    private int invalidTransactionCount = 0;

    public KafkaTransactionListener(TransactionProcessingService transactionProcessingService) {
        this.transactionProcessingService = transactionProcessingService;
    }

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
        
        // Process the transaction
        boolean processed = transactionProcessingService.processTransaction(transaction);
        
        if (processed) {
            validTransactionCount++;
            logger.info("Transaction #{} processed successfully", transactionCount);
        } else {
            invalidTransactionCount++;
            logger.warn("Transaction #{} was invalid and discarded", transactionCount);
        }
        
        logger.info("Transaction Summary - Total: {}, Valid: {}, Invalid: {}", 
                   transactionCount, validTransactionCount, invalidTransactionCount);
    }
}
