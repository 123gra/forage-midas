package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private IncentiveService incentiveService;

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);

        // Find sender and recipient by ID
        UserRecord sender = userService.findUserById(transaction.getSenderId());
        UserRecord recipient = userService.findUserById(transaction.getRecipientId());

        // Validate transaction
        if (sender == null) {
            logger.warn("Transaction rejected: Sender with ID {} not found", transaction.getSenderId());
            return false;
        }

        if (recipient == null) {
            logger.warn("Transaction rejected: Recipient with ID {} not found", transaction.getRecipientId());
            return false;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction rejected: Insufficient balance. Sender {} has {} but needs {}", 
                       sender.getName(), sender.getBalance(), transaction.getAmount());
            return false;
        }

        // Process valid transaction
        logger.info("Transaction valid. Processing: {} -> {} (${})", 
                   sender.getName(), recipient.getName(), transaction.getAmount());

        // Get incentive from incentive API
        Incentive incentive = incentiveService.getIncentive(transaction);
        logger.info("Incentive received: {}", incentive.getAmount());

        // Update balances
        // Sender pays the transaction amount
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        // Recipient receives transaction amount + incentive
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive.getAmount());

        // Save updated users
        userService.saveUser(sender);
        userService.saveUser(recipient);

        // Create and save transaction record with incentive
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive.getAmount());
        transactionRecordRepository.save(transactionRecord);

        logger.info("Transaction processed successfully. New balances - {}: {}, {}: {} (incentive: {})", 
                   sender.getName(), sender.getBalance(), 
                   recipient.getName(), recipient.getBalance(),
                   incentive.getAmount());

        return true;
    }
}
