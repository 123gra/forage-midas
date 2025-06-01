package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.foundation.Incentive;
import org.springframework.beans.factory.annotation.Value;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;
    @Value("${incentive.api.url:http://localhost:8081/incentive}")
    private String incentiveApiUrl;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository,
                               RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void handle(Transaction t) {
        logger.info("Received transaction: {}", t);
        UserRecord sender = userRepository.findById(t.getSenderId());
        UserRecord recipient = userRepository.findById(t.getRecipientId());
        if (sender != null && recipient != null && sender.getBalance() >= t.getAmount()) {
            sender.setBalance(sender.getBalance() - t.getAmount());
            // call incentive API and update recipient balance
            Incentive incentive = restTemplate.postForObject(incentiveApiUrl, t, Incentive.class);
            float incentiveAmount = (incentive != null ? incentive.getAmount() : 0f);
            recipient.setBalance(recipient.getBalance() + t.getAmount() + incentiveAmount);
            userRepository.save(sender);
            userRepository.save(recipient);
            TransactionRecord record = new TransactionRecord(sender, recipient, t.getAmount(), incentiveAmount);
            transactionRecordRepository.save(record);
            logger.info("Recorded transaction: {}", record);
        } else {
            logger.warn("Invalid transaction, discarded: {}", t);
        }
    }
} 