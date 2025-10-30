package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // collect first 4 amounts for easy submission
    private final AtomicInteger counter = new AtomicInteger(0);
    private final List<Float> firstFourAmounts = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory", groupId = "midas-core-group")
    @Transactional
    public void listen(Transaction tx) {
        // Spring will pass a Transaction because our consumerFactory/JsonDeserializer
        // are configured
        log.info("RECEIVED TRANSACTION -> senderId: {}, recipientId: {}, amount: {}",
                tx.getSenderId(), tx.getRecipientId(), tx.getAmount());

        int idx = counter.getAndIncrement();
        if (idx < 4) {
            firstFourAmounts.add(tx.getAmount());
            if (firstFourAmounts.size() == 4) {
                log.info("FIRST FOUR TRANSACTION AMOUNTS: {}", firstFourAmounts);
            }
        }

        // Task 3: Validate and persist transaction
        validateAndPersistTransaction(tx);
    }

    @Transactional
    private void validateAndPersistTransaction(Transaction tx) {
        long senderId = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        float amount = tx.getAmount();

        // Validate: Check if sender exists
        UserRecord sender = userRepository.findById(senderId);
        if (sender == null) {
            log.warn("INVALID TRANSACTION: Sender with id {} does not exist", senderId);
            return;
        }

        // Validate: Check if recipient exists
        UserRecord recipient = userRepository.findById(recipientId);
        if (recipient == null) {
            log.warn("INVALID TRANSACTION: Recipient with id {} does not exist", recipientId);
            return;
        }

        // Validate: Check if sender has sufficient balance
        if (sender.getBalance() < amount) {
            log.warn("INVALID TRANSACTION: Sender has insufficient balance. Current balance: {}, Required: {}",
                    sender.getBalance(), amount);
            return;
        }

        // Transaction is valid - persist it
        try {
            // Create and save the transaction record
            TransactionRecord txRecord = new TransactionRecord(sender, recipient, amount);
            transactionRepository.save(txRecord);
            log.info("TRANSACTION RECORD SAVED: {}", txRecord);

            // Update balances
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);

            // Save updated user records
            userRepository.save(sender);
            userRepository.save(recipient);

            log.info("TRANSACTION PROCESSED: Sender {} balance: {}, Recipient {} balance: {}",
                    sender.getName(), sender.getBalance(), recipient.getName(), recipient.getBalance());
        } catch (Exception e) {
            log.error("ERROR processing transaction: {}", e.getMessage(), e);
        }
    }

    // optional helper for debugging
    public List<Float> getFirstFourAmounts() {
        return firstFourAmounts;
    }
}
