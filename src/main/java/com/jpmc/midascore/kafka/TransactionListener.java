package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    // collect first 4 amounts for easy submission
    private final AtomicInteger counter = new AtomicInteger(0);
    private final List<Float> firstFourAmounts = new CopyOnWriteArrayList<>();

    @KafkaListener(
        topics = "${general.kafka-topic}",
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "midas-core-group"
    )
    public void listen(Transaction tx) {
        // Spring will pass a Transaction because our consumerFactory/JsonDeserializer are configured
        log.info("RECEIVED TRANSACTION -> senderId: {}, recipientId: {}, amount: {}",
                tx.getSenderId(), tx.getRecipientId(), tx.getAmount());

        int idx = counter.getAndIncrement();
        if (idx < 4) {
            firstFourAmounts.add(tx.getAmount());
            if (firstFourAmounts.size() == 4) {
                log.info("FIRST FOUR TRANSACTION AMOUNTS: {}", firstFourAmounts);
            }
        }
        // Task 2: receive & deserialize only
    }

    // optional helper for debugging
    public List<Float> getFirstFourAmounts() {
        return firstFourAmounts;
    }
}
