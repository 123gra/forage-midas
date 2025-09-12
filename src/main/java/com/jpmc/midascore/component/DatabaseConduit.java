package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Database access component providing high-level operations for Midas Core
 */
@Component
@Transactional
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    // ==================== USER OPERATIONS ====================
    
    /**
     * Save or update a user record
     */
    public UserRecord saveUser(UserRecord userRecord) {
        return userRepository.save(userRecord);
    }
    
    /**
     * Find user by ID
     */
    public Optional<UserRecord> findUserById(long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }
    
    /**
     * Find all users
     */
    public List<UserRecord> findAllUsers() {
        return (List<UserRecord>) userRepository.findAll();
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Update user balance
     */
    public boolean updateUserBalance(long userId, float newBalance) {
        Optional<UserRecord> userOpt = findUserById(userId);
        if (userOpt.isPresent()) {
            UserRecord user = userOpt.get();
            user.setBalance(newBalance);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // ==================== TRANSACTION OPERATIONS ====================
    
    /**
     * Process and validate a transaction from Kafka
     * Only saves to database if ALL validation conditions are met
     * If validation fails, transaction is discarded with no database modifications
     * 
     * @param transaction The transaction to process
     * @return TransactionResult indicating success or failure with details
     */
    public TransactionResult processKafkaTransaction(Transaction transaction) {
        // Validate users exist
        Optional<UserRecord> senderOpt = findUserById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = findUserById(transaction.getRecipientId());
        
        if (senderOpt.isEmpty()) {
            return TransactionResult.failed("Sender with ID " + transaction.getSenderId() + " not found");
        }
        
        if (recipientOpt.isEmpty()) {
            return TransactionResult.failed("Recipient with ID " + transaction.getRecipientId() + " not found");
        }
        
        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();
        
        // Validate amount is positive
        if (transaction.getAmount() <= 0) {
            return TransactionResult.failed("Transaction amount must be positive, got: " + transaction.getAmount());
        }
        
        // Validate sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            return TransactionResult.failed(
                String.format("Insufficient balance for sender %d. Balance: %.2f, Required: %.2f", 
                            transaction.getSenderId(), sender.getBalance(), transaction.getAmount()));
        }
        
        // Validate sender and recipient are different
        if (transaction.getSenderId() == transaction.getRecipientId()) {
            return TransactionResult.failed("Sender and recipient cannot be the same user");
        }
        
        // All validations passed - process the transfer
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());
        
        // Save updated balances
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
        TransactionRecord savedRecord = transactionRecordRepository.save(transactionRecord);
        
        return TransactionResult.success("Transaction processed successfully", savedRecord.getId());
    }
    
    /**
     * Find transaction record by ID
     */
    public Optional<TransactionRecord> findTransactionRecordById(long id) {
        return transactionRecordRepository.findById(id);
    }
    
    /**
     * Find all transaction records for a user
     */
    public List<TransactionRecord> findUserTransactionRecords(long userId) {
        return transactionRecordRepository.findAllTransactionsByUserId(userId);
    }
    
    /**
     * Find recent transaction records (last 24 hours)
     */
    public List<TransactionRecord> findRecentTransactionRecords() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return transactionRecordRepository.findByTimestampBetween(yesterday, LocalDateTime.now());
    }
    
    /**
     * Get transaction statistics for a user
     */
    public TransactionStats getUserTransactionStats(long userId) {
        Float totalSent = transactionRecordRepository.getTotalSentAmount(userId);
        Float totalReceived = transactionRecordRepository.getTotalReceivedAmount(userId);
        
        return new TransactionStats(
            totalSent != null ? totalSent : 0.0f,
            totalReceived != null ? totalReceived : 0.0f
        );
    }
    
    // ==================== UTILITY CLASSES ====================
    
    /**
     * Result of transaction processing
     */
    public static class TransactionResult {
        private final boolean success;
        private final String message;
        private final Long transactionRecordId;
        
        private TransactionResult(boolean success, String message, Long transactionRecordId) {
            this.success = success;
            this.message = message;
            this.transactionRecordId = transactionRecordId;
        }
        
        public static TransactionResult success(String message, Long transactionRecordId) {
            return new TransactionResult(true, message, transactionRecordId);
        }
        
        public static TransactionResult failed(String message) {
            return new TransactionResult(false, message, null);
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Long getTransactionRecordId() { return transactionRecordId; }
        
        @Override
        public String toString() {
            return String.format("TransactionResult{success=%s, message='%s', transactionRecordId=%s}",
                                success, message, transactionRecordId);
        }
    }
    
    /**
     * Simple data class for transaction statistics
     */
    public static class TransactionStats {
        private final float totalSent;
        private final float totalReceived;
        
        public TransactionStats(float totalSent, float totalReceived) {
            this.totalSent = totalSent;
            this.totalReceived = totalReceived;
        }
        
        public float getTotalSent() { return totalSent; }
        public float getTotalReceived() { return totalReceived; }
        public float getNetAmount() { return totalReceived - totalSent; }
        
        @Override
        public String toString() {
            return String.format("TransactionStats{totalSent=%.2f, totalReceived=%.2f, net=%.2f}",
                totalSent, totalReceived, getNetAmount());
        }
    }
}
