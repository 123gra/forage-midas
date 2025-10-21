package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Incentive;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final List<Float> amounts = new ArrayList<>();

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    public List<Float> getAmounts() {
        return amounts;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        // record first 4 amounts for debugging / test observation
        if (amounts.size() < 4) {
            amounts.add(transaction.getAmount());
            logger.info("Recorded amount #{}: {}", amounts.size(), transaction.getAmount());
        }

        // Validation & persistence
        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null) {
            logger.warn("Discarding transaction: sender {} not found", senderId);
            return;
        }
        if (recipient == null) {
            logger.warn("Discarding transaction: recipient {} not found", recipientId);
            return;
        }

        if (sender.getBalance() < amount) {
            logger.warn("Discarding transaction: insufficient funds for sender {} (balance={} amount={})", senderId, sender.getBalance(), amount);
            return;
        }

        // adjust balances for the transaction amount
        sender.setBalance(sender.getBalance() - amount);

        // call incentive API (post Transaction -> returns Incentive JSON)
        float incentiveAmount = 0f;
        try {
            Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
            if (incentive != null) {
                incentiveAmount = incentive.getAmount();
            }
        } catch (Exception e) {
            logger.warn("Incentive API call failed for transaction {}: {}", transaction, e.toString());
            incentiveAmount = 0f;
        }

        // add transaction amount + incentive to recipient balance (incentive not deducted from sender)
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // persist updated users and the transaction record
        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRecordRepository.save(record);

        logger.info("Persisted transaction id={} amount={} incentive={} from {} to {}", record.getId(), amount, incentiveAmount, senderId, recipientId);
    }
}
