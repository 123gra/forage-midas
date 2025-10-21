package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final List<Float> amounts = new ArrayList<>();

    public List<Float> getAmounts() {
        return amounts;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        if (amounts.size() < 4) {
            amounts.add(transaction.getAmount());
            logger.info("Recorded amount #{}: {}", amounts.size(), transaction.getAmount());
        }
    }
}
