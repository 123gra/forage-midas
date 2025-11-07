package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    @Value("${general.kafka-topic}")
    private String topic;

    @Autowired
    private DatabaseConduit databaseConduit;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void receiveTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        boolean processed = databaseConduit.processTransaction(
            transaction.getSenderId(),
            transaction.getRecipientId(),
            transaction.getAmount()
        );

        if (processed) {
            logger.info("Transaction processed successfully: senderId={}, recipientId={}, amount={}", 
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        } else {
            logger.warn("Transaction discarded (validation failed): senderId={}, recipientId={}, amount={}", 
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        }
    }
}