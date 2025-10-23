package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    @Transactional
    public void handleTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        
        // Validate transaction
        if (isValidTransaction(transaction)) {
            // Process the transaction
            processTransaction(transaction);
            logger.info("Transaction processed successfully: {}", transaction);
        } else {
            logger.warn("Transaction validation failed, discarding: {}", transaction);
        }
    }
    
    private boolean isValidTransaction(Transaction transaction) {
        // Check if senderId is valid
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Invalid senderId: {}", transaction.getSenderId());
            return false;
        }
        
        // Check if recipientId is valid
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Invalid recipientId: {}", transaction.getRecipientId());
            return false;
        }
        
        // Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance for sender {}: required {}, available {}", 
                       sender.getName(), transaction.getAmount(), sender.getBalance());
            return false;
        }
        
        return true;
    }
    
    private void processTransaction(Transaction transaction) {
        // Get sender and recipient from database
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        
        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());
        
        // Save updated user records
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRepository.save(transactionRecord);
        
        logger.info("Transaction recorded: {} -> {}: {}", 
                   sender.getName(), recipient.getName(), transaction.getAmount());
    }
}
