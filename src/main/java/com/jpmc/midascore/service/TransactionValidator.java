package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for validating transactions according to business rules
 */
@Service
public class TransactionValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionValidator.class);
    
    private final UserRepository userRepository;
    
    public TransactionValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Validates a transaction according to business rules:
     * 1. SenderId must be valid (user exists)
     * 2. RecipientId must be valid (user exists)  
     * 3. Sender must have sufficient balance
     * 
     * @param transaction The transaction to validate
     * @return ValidationResult containing validation status and details
     */
    public ValidationResult validateTransaction(Transaction transaction) {
        logger.debug("Validating transaction: {}", transaction);
        
        // Check if transaction is null
        if (transaction == null) {
            return ValidationResult.invalid("Transaction cannot be null");
        }
        
        // Check if amount is positive
        if (transaction.getAmount() <= 0) {
            return ValidationResult.invalid("Transaction amount must be positive, got: " + transaction.getAmount());
        }
        
        // Validate sender
        ValidationResult senderValidation = validateSender(transaction.getSenderId(), transaction.getAmount());
        if (!senderValidation.isValid()) {
            return senderValidation;
        }
        
        // Validate recipient
        ValidationResult recipientValidation = validateRecipient(transaction.getRecipientId());
        if (!recipientValidation.isValid()) {
            return recipientValidation;
        }
        
        // Check that sender and recipient are different
        if (transaction.getSenderId() == transaction.getRecipientId()) {
            return ValidationResult.invalid("Sender and recipient cannot be the same user");
        }
        
        logger.debug("Transaction validation successful: {}", transaction);
        return ValidationResult.valid("Transaction is valid");
    }
    
    /**
     * Validates that the sender exists and has sufficient balance
     */
    private ValidationResult validateSender(long senderId, float amount) {
        UserRecord sender = userRepository.findById(senderId);
        
        if (sender == null) {
            logger.warn("Invalid sender ID: {}", senderId);
            return ValidationResult.invalid("Sender with ID " + senderId + " not found");
        }
        
        if (sender.getBalance() < amount) {
            logger.warn("Insufficient balance for sender ID: {}. Balance: {}, Required: {}", 
                       senderId, sender.getBalance(), amount);
            return ValidationResult.invalid(
                String.format("Insufficient balance for sender %d. Balance: %.2f, Required: %.2f", 
                            senderId, sender.getBalance(), amount));
        }
        
        logger.debug("Sender validation successful for ID: {}", senderId);
        return ValidationResult.valid("Sender is valid");
    }
    
    /**
     * Validates that the recipient exists
     */
    private ValidationResult validateRecipient(long recipientId) {
        UserRecord recipient = userRepository.findById(recipientId);
        
        if (recipient == null) {
            logger.warn("Invalid recipient ID: {}", recipientId);
            return ValidationResult.invalid("Recipient with ID " + recipientId + " not found");
        }
        
        logger.debug("Recipient validation successful for ID: {}", recipientId);
        return ValidationResult.valid("Recipient is valid");
    }
    
    /**
     * Result of transaction validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public static ValidationResult valid(String message) {
            return new ValidationResult(true, message);
        }
        
        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, message='%s'}", valid, message);
        }
    }
}
