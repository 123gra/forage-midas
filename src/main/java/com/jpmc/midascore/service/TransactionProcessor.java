package com.jpmc.midascore.service;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for processing transactions received from Kafka
 * Handles validation, recording, and balance updates
 */
@Service
public class TransactionProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);
    
    private final DatabaseConduit databaseConduit;
    
    public TransactionProcessor(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }
    
    /**
     * Processes a transaction received from Kafka
     * Either saves successfully to database (if all conditions met) or discards completely
     * 
     * @param transaction The transaction to process
     * @return ProcessingResult containing the result of the processing
     */
    @Transactional
    public ProcessingResult processTransaction(Transaction transaction) {
        logger.info("Processing Kafka transaction: {}", transaction);
        
        try {
            // Process transaction using DatabaseConduit (validates and saves or discards)
            DatabaseConduit.TransactionResult result = databaseConduit.processKafkaTransaction(transaction);
            
            if (result.isSuccess()) {
                logger.info("Transaction processed successfully: {}", result.getMessage());
                return ProcessingResult.success(
                    result.getMessage(),
                    result.getTransactionRecordId()
                );
            } else {
                logger.warn("Transaction processing failed (discarded): {}", result.getMessage());
                return ProcessingResult.validationFailed(
                    result.getMessage(),
                    null  // No transaction record ID since transaction was discarded
                );
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error processing transaction: {}", transaction, e);
            return ProcessingResult.error(
                "Unexpected error: " + e.getMessage(),
                null
            );
        }
    }
    
    /**
     * Result of transaction processing
     */
    public static class ProcessingResult {
        public enum Status {
            SUCCESS,
            VALIDATION_FAILED,
            PROCESSING_FAILED,
            ERROR
        }
        
        private final Status status;
        private final String message;
        private final Long transactionId;
        
        private ProcessingResult(Status status, String message, Long transactionId) {
            this.status = status;
            this.message = message;
            this.transactionId = transactionId;
        }
        
        public static ProcessingResult success(String message, Long transactionId) {
            return new ProcessingResult(Status.SUCCESS, message, transactionId);
        }
        
        public static ProcessingResult validationFailed(String message, Long transactionId) {
            return new ProcessingResult(Status.VALIDATION_FAILED, message, transactionId);
        }
        
        public static ProcessingResult processingFailed(String message, Long transactionId) {
            return new ProcessingResult(Status.PROCESSING_FAILED, message, transactionId);
        }
        
        public static ProcessingResult error(String message, Long transactionId) {
            return new ProcessingResult(Status.ERROR, message, transactionId);
        }
        
        public Status getStatus() {
            return status;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Long getTransactionId() {
            return transactionId;
        }
        
        public boolean isSuccessful() {
            return status == Status.SUCCESS;
        }
        
        @Override
        public String toString() {
            return String.format("ProcessingResult{status=%s, message='%s', transactionId=%s}", 
                               status, message, transactionId);
        }
    }
}
