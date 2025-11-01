package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final UserRepository userRepository;
    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;

    public KafkaConsumer(UserRepository userRepository, DatabaseConduit databaseConduit, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.databaseConduit = databaseConduit;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void receiveTransaction(Transaction transaction) {
        logger.debug("Received transaction: {}", transaction);

        // validate sender and recipient exist
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender == null) {
            logger.info("Discarding transaction: sender {} not found", transaction.getSenderId());
            return;
        }
        if (recipient == null) {
            logger.info("Discarding transaction: recipient {} not found", transaction.getRecipientId());
            return;
        }

        float amount = transaction.getAmount();
        if (sender.getBalance() < amount) {
            logger.info("Discarding transaction: sender {} has insufficient balance (has={}, needs={})", sender.getId(), sender.getBalance(), amount);
            return;
        }

        // apply transaction amounts
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // call incentives API
        float incentiveAmount = 0f;
        try {
            Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
            if (incentive != null) {
                incentiveAmount = incentive.getAmount();
            }
        } catch (RestClientException e) {
            logger.warn("Failed to call incentives API, defaulting incentive to 0: {}", e.toString());
            incentiveAmount = 0f;
        }

        // apply incentive to recipient only
        if (incentiveAmount > 0f) {
            recipient.setBalance(recipient.getBalance() + incentiveAmount);
        }

        // persist changes
        databaseConduit.save(sender);
        databaseConduit.save(recipient);

        TransactionRecord tr = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        databaseConduit.saveTransaction(tr);

        logger.info("Persisted transaction {} -> {} amount={} incentive={}", sender.getId(), recipient.getId(), amount, incentiveAmount);
    }
}