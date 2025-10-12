package com.jpmc.midascore.listener;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
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
    private TransactionRecordRepository transactionRecordRepository;
    
    @KafkaListener(topics = "${general.kafka-topic}")
    @Transactional
    public void handleTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        
        // For debugging purposes - log the transaction details
        logger.debug("Transaction details - SenderId: {}, RecipientId: {}, Amount: {}", 
                    transaction.getSenderId(), 
                    transaction.getRecipientId(), 
                    transaction.getAmount());
        
        // Validate the transaction
        if (isValidTransaction(transaction)) {
            processTransaction(transaction);
            logger.info("Transaction processed successfully: {}", transaction);
        } else {
            logger.warn("Transaction discarded - validation failed: {}", transaction);
        }
    }
    
    private boolean isValidTransaction(Transaction transaction) {
        // Check if sender exists and is valid
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            logger.debug("Invalid senderId: {}", transaction.getSenderId());
            return false;
        }
        
        // Check if recipient exists and is valid
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            logger.debug("Invalid recipientId: {}", transaction.getRecipientId());
            return false;
        }
        
        // Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.debug("Insufficient balance - Sender: {}, Balance: {}, Required: {}", 
                        sender.getName(), sender.getBalance(), transaction.getAmount());
            return false;
        }
        
        return true;
    }
    
    private void processTransaction(Transaction transaction) {
        // Get sender and recipient
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
        transactionRecordRepository.save(transactionRecord);
        
        logger.debug("Balance updated - Sender: {} ({}), Recipient: {} ({})", 
                    sender.getName(), sender.getBalance(), 
                    recipient.getName(), recipient.getBalance());
    }
}