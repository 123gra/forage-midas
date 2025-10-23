package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private RestTemplate restTemplate;

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
        
        // Call incentive API to get incentive amount
        Incentive incentive = getIncentiveFromAPI(transaction);
        float incentiveAmount = incentive.getAmount();
        
        // Update balances
        // Sender pays the transaction amount (no incentive deducted from sender)
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        // Recipient gets transaction amount + incentive
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);
        
        // Save updated user records
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record with incentive
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        transactionRepository.save(transactionRecord);
        
        logger.info("Transaction recorded: {} -> {}: {} (incentive: {})", 
                   sender.getName(), recipient.getName(), transaction.getAmount(), incentiveAmount);
    }
    
    private Incentive getIncentiveFromAPI(Transaction transaction) {
        try {
            String apiUrl = "http://localhost:8080/incentive";
            Incentive incentive = restTemplate.postForObject(apiUrl, transaction, Incentive.class);
            logger.info("Received incentive from API: {}", incentive);
            return incentive;
        } catch (Exception e) {
            logger.warn("Failed to get incentive from API, using 0: {}", e.getMessage());
            return new Incentive(0.0f);
        }
    }
}
