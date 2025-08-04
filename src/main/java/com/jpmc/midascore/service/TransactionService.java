package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    private static final String INCENTIVE_API = "http://localhost:8080/incentive";

    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    public float getIncentive(Transaction transaction) {
        try {
            Balance response = restTemplate.postForObject(INCENTIVE_API, transaction, Balance.class);
            return response != null ? response.getAmount(): 0.0f;
        } catch (Exception e) {
            logger.error("Failed to get incentive for transaction: {}", transaction, e);
            return 0.0f;
        }
    }


    @Transactional
    public void processTransaction(Transaction transaction) {
        Optional<UserRecord> senderOpt = Optional.ofNullable(userRepository.findById(transaction.getSenderId()));
        Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(transaction.getRecipientId()));

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            logger.warn("Invalid sender or recipient ID");
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient funds for sender: {}", sender.getId());
            return;
        }

        float incentive = getIncentive(transaction);

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(transaction.getAmount());
        record.setIncentive(incentive);
        transactionRepository.save(record);

        if ("waldorf".equalsIgnoreCase(sender.getName())) {
            logger.info("Current Waldorf Balance: {}", sender.getBalance());
        }

        if ("wilbur".equalsIgnoreCase(sender.getName()) || "wilbur".equalsIgnoreCase(recipient.getName())) {
            logger.info("Wilbur's balance: {}",
                    "wilbur".equalsIgnoreCase(sender.getName()) ? sender.getBalance() : recipient.getBalance());
        }
    }


    public int getBalance(String userId) {
        try {
            Long id = Long.parseLong(userId);
            Optional<UserRecord> userOpt = userRepository.findById(id);
            return userOpt.map(user -> (int) Math.floor(user.getBalance())).orElse(0);
        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", userId);
            return 0;
        }
    }

}
