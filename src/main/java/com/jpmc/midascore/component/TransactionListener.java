package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate; 

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // <--- Initialize RestTemplate

    public TransactionListener(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender == null || recipient == null) {
            logger.warn("Transaction invalid: User not found");
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction invalid: Insufficient funds");
            return;
        }
        
        // 1. Call the Incentive API
        Incentive incentive = restTemplate.postForObject(
            "http://localhost:8080/incentive", 
            transaction, 
            Incentive.class
        );
        
        float incentiveAmount = incentive.getAmount();

        // 2. Update Balances
        // Sender loses money (just the transaction amount)
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        
        // Recipient gets money + incentive
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // 3. Save Record (with incentive)
        TransactionRecord record = new TransactionRecord(
            sender, 
            recipient, 
            transaction.getAmount(), 
            incentiveAmount
        );
        transactionRecordRepository.save(record);
        
        // Helper log for the task answer
        if (recipient.getName().equals("wilbur")) {
             logger.info("!!! WILBUR NEW BALANCE: {} !!!", recipient.getBalance());
        }
        if (sender.getName().equals("wilbur")) {
             logger.info("!!! WILBUR NEW BALANCE: {} !!!", sender.getBalance());
        }
    }
}