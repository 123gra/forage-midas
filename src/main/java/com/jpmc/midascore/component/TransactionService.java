package com.jpmc.midascore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveService incentiveService;

    public TransactionService(UserRepository userRepository, 
                            TransactionRecordRepository transactionRecordRepository,
                            IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveService = incentiveService;
    }

    /**
     * Process a transaction: validate it, and if valid, record it to the database
     * and update user balances with incentives.
     *
     * @param transaction The transaction to process
     * @return true if transaction was valid and processed, false if discarded
     */
    @Transactional
    public boolean processTransaction(Transaction transaction) {
        // 1. Validate senderId exists
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Transaction discarded: Invalid sender ID {}", transaction.getSenderId());
            return false;
        }

        // 2. Validate recipientId exists
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Transaction discarded: Invalid recipient ID {}", transaction.getRecipientId());
            return false;
        }

        // 3. Validate sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction discarded: Insufficient balance for user {} (balance: {}, amount: {})",
                    sender.getName(), sender.getBalance(), transaction.getAmount());
            return false;
        }

        // 4. Get incentive from external API
        float incentive = incentiveService.getIncentive(transaction);

        // Transaction is valid - process it
        // 5. Create and save TransactionRecord with incentive
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive);
        transactionRecordRepository.save(record);

        // 6. Update balances
        // Sender loses only the transaction amount (incentive NOT deducted from sender)
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        // Recipient gains transaction amount + incentive
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);

        logger.info("Transaction processed: {} sent {} to {} with incentive {} (new balances: sender={}, recipient={})",
                sender.getName(), transaction.getAmount(), recipient.getName(), incentive,
                sender.getBalance(), recipient.getBalance());

        return true;
    }
}