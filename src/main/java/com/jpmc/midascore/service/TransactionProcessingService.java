package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessingService.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionProcessingService(UserRepository userRepository, 
                                      TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        try {
            logger.debug("Processing transaction: {}", transaction);

            // Validate sender ID
            UserRecord sender = userRepository.findById(transaction.getSenderId());
            if (sender == null) {
                logger.warn("Invalid sender ID: {}", transaction.getSenderId());
                return false;
            }

            // Validate recipient ID
            UserRecord recipient = userRepository.findById(transaction.getRecipientId());
            if (recipient == null) {
                logger.warn("Invalid recipient ID: {}", transaction.getRecipientId());
                return false;
            }

            // Validate sender balance
            if (sender.getBalance() < transaction.getAmount()) {
                logger.warn("Insufficient balance for sender {}. Balance: {}, Required: {}", 
                          sender.getName(), sender.getBalance(), transaction.getAmount());
                return false;
            }

            // Process the transaction
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());

            // Save updated user records
            userRepository.save(sender);
            userRepository.save(recipient);

            // Record the transaction
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRecordRepository.save(transactionRecord);

            logger.info("Transaction processed successfully: {} -> {} ({})", 
                       sender.getName(), recipient.getName(), transaction.getAmount());
            logger.info("Updated balances - {}: {}, {}: {}", 
                       sender.getName(), sender.getBalance(), 
                       recipient.getName(), recipient.getBalance());

            return true;

        } catch (Exception e) {
            logger.error("Error processing transaction: {}", transaction, e);
            return false;
        }
    }
}
