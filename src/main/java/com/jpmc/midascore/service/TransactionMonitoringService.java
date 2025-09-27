package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.TransactionStatus;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.service.TransactionProcessor.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for monitoring transaction processing and handling errors
 */
@Service
public class TransactionMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionMonitoringService.class);
    
    private final TransactionRepository transactionRepository;
    
    public TransactionMonitoringService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * Logs transaction processing result asynchronously
     */
    @Async
    public CompletableFuture<Void> logProcessingResult(ProcessingResult result, Transaction originalTransaction) {
        try {
            switch (result.getStatus()) {
                case SUCCESS:
                    logSuccessfulTransaction(result, originalTransaction);
                    break;
                case VALIDATION_FAILED:
                    logValidationFailure(result, originalTransaction);
                    break;
                case PROCESSING_FAILED:
                    logProcessingFailure(result, originalTransaction);
                    break;
                case ERROR:
                    logProcessingError(result, originalTransaction);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error logging transaction processing result", e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Checks for failed transactions and alerts if threshold is exceeded
     */
    public void checkFailedTransactionThreshold() {
        try {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            List<Transaction> recentFailedTransactions = transactionRepository
                .findByTimestampBetween(oneHourAgo, LocalDateTime.now())
                .stream()
                .filter(t -> t.getStatus() == TransactionStatus.FAILED)
                .toList();
            
            if (recentFailedTransactions.size() > 10) { // Configurable threshold
                logger.warn("HIGH FAILURE RATE ALERT: {} failed transactions in the last hour", 
                           recentFailedTransactions.size());
                
                // In a production system, you might want to:
                // - Send alerts to monitoring systems
                // - Trigger circuit breakers
                // - Send notifications to operations team
            }
        } catch (Exception e) {
            logger.error("Error checking failed transaction threshold", e);
        }
    }
    
    /**
     * Gets transaction processing statistics
     */
    public TransactionStats getTransactionStats() {
        try {
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusDays(1);
            List<Transaction> recentTransactions = transactionRepository
                .findByTimestampBetween(twentyFourHoursAgo, LocalDateTime.now());
            
            long totalTransactions = recentTransactions.size();
            long successfulTransactions = recentTransactions.stream()
                .mapToLong(t -> t.getStatus() == TransactionStatus.COMPLETED ? 1 : 0)
                .sum();
            long failedTransactions = recentTransactions.stream()
                .mapToLong(t -> t.getStatus() == TransactionStatus.FAILED ? 1 : 0)
                .sum();
            long pendingTransactions = recentTransactions.stream()
                .mapToLong(t -> t.getStatus() == TransactionStatus.PENDING ? 1 : 0)
                .sum();
            
            return new TransactionStats(totalTransactions, successfulTransactions, 
                                      failedTransactions, pendingTransactions);
        } catch (Exception e) {
            logger.error("Error getting transaction stats", e);
            return new TransactionStats(0, 0, 0, 0);
        }
    }
    
    private void logSuccessfulTransaction(ProcessingResult result, Transaction originalTransaction) {
        logger.info("SUCCESS - Transaction ID: {}, Original: {}, Message: {}", 
                   result.getTransactionId(), originalTransaction, result.getMessage());
    }
    
    private void logValidationFailure(ProcessingResult result, Transaction originalTransaction) {
        logger.warn("VALIDATION_FAILED - Transaction ID: {}, Original: {}, Reason: {}", 
                   result.getTransactionId(), originalTransaction, result.getMessage());
        
        // Additional validation failure logging
        if (originalTransaction != null) {
            logger.warn("Failed transaction details - Sender: {}, Recipient: {}, Amount: {}", 
                       originalTransaction.getSenderId(), 
                       originalTransaction.getRecipientId(),
                       originalTransaction.getAmount());
        }
    }
    
    private void logProcessingFailure(ProcessingResult result, Transaction originalTransaction) {
        logger.error("PROCESSING_FAILED - Transaction ID: {}, Original: {}, Reason: {}", 
                    result.getTransactionId(), originalTransaction, result.getMessage());
        
        // This indicates a system issue that needs attention
        logger.error("ALERT: Transaction processing failure may indicate system issues");
    }
    
    private void logProcessingError(ProcessingResult result, Transaction originalTransaction) {
        logger.error("PROCESSING_ERROR - Transaction ID: {}, Original: {}, Error: {}", 
                    result.getTransactionId(), originalTransaction, result.getMessage());
        
        // This indicates a serious system error
        logger.error("CRITICAL: Unexpected error in transaction processing pipeline");
    }
    
    /**
     * Transaction statistics data class
     */
    public static class TransactionStats {
        private final long totalTransactions;
        private final long successfulTransactions;
        private final long failedTransactions;
        private final long pendingTransactions;
        
        public TransactionStats(long totalTransactions, long successfulTransactions, 
                              long failedTransactions, long pendingTransactions) {
            this.totalTransactions = totalTransactions;
            this.successfulTransactions = successfulTransactions;
            this.failedTransactions = failedTransactions;
            this.pendingTransactions = pendingTransactions;
        }
        
        public long getTotalTransactions() { return totalTransactions; }
        public long getSuccessfulTransactions() { return successfulTransactions; }
        public long getFailedTransactions() { return failedTransactions; }
        public long getPendingTransactions() { return pendingTransactions; }
        
        public double getSuccessRate() {
            return totalTransactions > 0 ? (double) successfulTransactions / totalTransactions * 100 : 0;
        }
        
        public double getFailureRate() {
            return totalTransactions > 0 ? (double) failedTransactions / totalTransactions * 100 : 0;
        }
        
        @Override
        public String toString() {
            return String.format("TransactionStats{total=%d, successful=%d, failed=%d, pending=%d, successRate=%.2f%%, failureRate=%.2f%%}",
                totalTransactions, successfulTransactions, failedTransactions, pendingTransactions,
                getSuccessRate(), getFailureRate());
        }
    }
}
