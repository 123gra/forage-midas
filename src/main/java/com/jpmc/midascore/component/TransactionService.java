package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.Transaction;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: senderId={}, recipientId={}, amount={}",
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());

        // Validate transaction
        if (!isValidTransaction(transaction)) {
            logger.warn("Invalid transaction, discarding: {}", transaction);
            return false;
        }

        // Get sender and recipient
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            logger.warn("Sender or recipient not found, discarding transaction");
            return false;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance. Sender {} has {}, transaction amount is {}",
                    sender.getName(), sender.getBalance(), transaction.getAmount());
            return false;
        }

        // Get incentive amount from API
        float incentiveAmount = incentiveService.getIncentiveAmount(transaction);

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated user records
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record with incentive
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        transactionRecordRepository.save(record);

        logger.info("Transaction processed successfully. Sender {} new balance: {}, Recipient {} new balance: {} (includes incentive: {})",
                sender.getName(), sender.getBalance(), recipient.getName(), recipient.getBalance(), incentiveAmount);

        return true;
    }

    public float getUserBalance(String userName) {
        Optional<UserRecord> userOpt = userRepository.findByName(userName);
        if (userOpt.isPresent()) {
            return userOpt.get().getBalance();
        }
        return -1;
    }

    private boolean isValidTransaction(Transaction transaction) {
        // Check if senderId and recipientId are valid (greater than 0)
        if (transaction.getSenderId() <= 0 || transaction.getRecipientId() <= 0) {
            return false;
        }

        // Check if amount is positive
        if (transaction.getAmount() <= 0) {
            return false;
        }

        // Check if sender and recipient are different
        if (transaction.getSenderId() == transaction.getRecipientId()) {
            return false;
        }

        return true;
    }
}