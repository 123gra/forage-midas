package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);

        // Find sender and recipient by ID
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        // Validate transaction
        if (senderOpt.isEmpty()) {
            logger.warn("Transaction rejected: Sender with ID {} not found", transaction.getSenderId());
            return false;
        }

        if (recipientOpt.isEmpty()) {
            logger.warn("Transaction rejected: Recipient with ID {} not found", transaction.getRecipientId());
            return false;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction rejected: Insufficient balance. Sender {} has {} but needs {}", 
                       sender.getName(), sender.getBalance(), transaction.getAmount());
            return false;
        }

        // Process valid transaction
        logger.info("Transaction valid. Processing: {} -> {} (${})", 
                   sender.getName(), recipient.getName(), transaction.getAmount());

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRecordRepository.save(transactionRecord);

        logger.info("Transaction processed successfully. New balances - {}: {}, {}: {}", 
                   sender.getName(), sender.getBalance(), 
                   recipient.getName(), recipient.getBalance());

        return true;
    }
}
