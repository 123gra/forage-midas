package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionListener(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    @Transactional
    public void receiveTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        
        // Validate transaction
        if (!isValidTransaction(transaction)) {
            logger.info("Transaction invalid, discarding: {}", transaction);
            return;
        }
        
        // Get sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        
        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());
        
        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRepository.save(transactionRecord);
        
        logger.info("Transaction processed successfully: {}", transaction);
        
        // Log waldorf's balance for debugging after each transaction
        logWaldorfBalance();
    }
    
    private void logWaldorfBalance() {
        try {
            Optional<UserRecord> waldorfOpt = userRepository.findByName("waldorf");
            if (waldorfOpt.isPresent()) {
                UserRecord waldorf = waldorfOpt.get();
                float balance = waldorf.getBalance();
                int balanceRounded = (int) Math.floor(balance);
                logger.info("=== Waldorf's current balance: {} (rounded down: {}) ===", balance, balanceRounded);
            }
        } catch (Exception e) {
            // Ignore if waldorf doesn't exist yet or method not available
            logger.debug("Could not query waldorf balance: {}", e.getMessage());
        }
    }
    
    private boolean isValidTransaction(Transaction transaction) {
        // Check if senderId is valid (user exists)
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Invalid senderId: {}", transaction.getSenderId());
            return false;
        }
        
        // Check if recipientId is valid (user exists)
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Invalid recipientId: {}", transaction.getRecipientId());
            return false;
        }
        
        // Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance. Sender balance: {}, Transaction amount: {}", 
                       sender.getBalance(), transaction.getAmount());
            return false;
        }
        
        return true;
    }
}

