package com.jpmc.midascore.component;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TransactionService(UserRepository userRepository, 
                            TransactionRepository transactionRepository,
                            RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    public void handleTransaction(Transaction txn) {
        logger.info("Processing transaction: {}", txn);
        
        Long senderId = txn.getSenderId();
        Long recipientId = txn.getRecipientId();

        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        if (!senderOpt.isPresent() || !recipientOpt.isPresent()) {
            logger.error("Sender or recipient not found. Sender ID: {}, Recipient ID: {}", senderId, recipientId);
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < txn.getAmount()) {
            logger.warn("Insufficient balance. Sender ID: {}, Balance: {}, Amount: {}", 
                       senderId, sender.getBalance(), txn.getAmount());
            return;
        }

        try {
            // Call incentive API with timeout
            Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                txn,
                Incentive.class
            );

            float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;
            logger.info("Received incentive amount: {}", incentiveAmount);

            // Update balances
            sender.setBalance(sender.getBalance() - txn.getAmount());
            recipient.setBalance(recipient.getBalance() + txn.getAmount() + incentiveAmount);

            // Save transaction with incentive
            TransactionRecord record = new TransactionRecord(
                sender, 
                recipient, 
                txn.getAmount(), 
                incentiveAmount
            );
            transactionRepository.save(record);

            // Save updated users
            userRepository.save(sender);
            userRepository.save(recipient);

            logger.info("Transaction completed successfully. New balances - Sender {}: {}, Recipient {}: {}",
                       senderId, sender.getBalance(), recipientId, recipient.getBalance());

            // Debug output for Wilbur
            if ("wilbur".equalsIgnoreCase(recipient.getName())) {
                logger.info("WILBUR'S CURRENT BALANCE: {}", (int)Math.floor(recipient.getBalance()));
            }

        } catch (RestClientException e) {
            logger.error("Failed to call Incentive API: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Transaction processing failed: {}", e.getMessage(), e);
        }
    }
}