package com.jpmc.midascore.listener;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private TransactionRecordRepository transactionRecordRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
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
        
        // Call incentives API to get incentive amount
        Incentive incentive = getIncentive(transaction);
        double incentiveAmount = incentive != null && incentive.getAmount() != null ? 
            incentive.getAmount().doubleValue() : 0.0;
        
        logger.debug("Incentive amount calculated: {}", incentiveAmount);
        
        // Update balances - deduct from sender, add transaction amount + incentive to recipient
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance((float)(recipient.getBalance() + transaction.getAmount() + incentiveAmount));
        
        // Save updated user records
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRecordRepository.save(transactionRecord);
        
        logger.debug("Balance updated - Sender: {} ({}), Recipient: {} ({}) [Incentive: {}]", 
                    sender.getName(), sender.getBalance(), 
                    recipient.getName(), recipient.getBalance(), incentiveAmount);
    }
    
    private Incentive getIncentive(Transaction transaction) {
        try {
            String incentiveUrl = "http://localhost:8081/incentive";
            
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request entity with transaction data
            HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);
            
            // Call the incentives API
            Incentive incentive = restTemplate.postForObject(incentiveUrl, request, Incentive.class);
            
            logger.debug("Received incentive response: {}", incentive != null ? incentive.getAmount() : "null");
            return incentive;
            
        } catch (Exception e) {
            logger.error("Failed to get incentive for transaction: {}", transaction, e);
            return null;
        }
    }
}