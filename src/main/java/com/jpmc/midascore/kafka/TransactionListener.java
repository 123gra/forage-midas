package com.jpmc.midascore.kafka;


import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void handle(Transaction t) {
        logger.info("Received transaction: {}", t);
        UserRecord sender = userRepository.findById(t.getSenderId());
        UserRecord recipient = userRepository.findById(t.getRecipientId());
        if (sender != null && recipient != null && sender.getBalance() >= t.getAmount()) {
            sender.setBalance(sender.getBalance() - t.getAmount());
            recipient.setBalance(recipient.getBalance() + t.getAmount());
            userRepository.save(sender);
            userRepository.save(recipient);
            TransactionRecord record = new TransactionRecord(sender, recipient, t.getAmount());
            transactionRecordRepository.save(record);
            logger.info("Recorded transaction: {}", record);
        } else {
            logger.warn("Invalid transaction, discarded: {}", t);
        }
    }

}
