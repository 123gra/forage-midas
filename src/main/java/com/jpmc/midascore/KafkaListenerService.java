package com.jpmc.midascore;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpmc.midascore.foundation.Transaction;

/**
 * Kafka listener that receives Transaction objects from the topic defined
 * in application.yml under general.kafka-topic.
 */
@Component
public class KafkaListenerService {

    // A list to store received transactions (useful for testing)
    private static final List<Transaction> receivedTransactions =
            Collections.synchronizedList(new ArrayList<>());

    // This method automatically gets called whenever a new message is published to the Kafka topic
    @KafkaListener(topics = "${general.kafka-topic}")
    public void receive(Transaction transaction) {
        receivedTransactions.add(transaction);
        System.out.println("✅ Received Transaction from Kafka: " + transaction);
    }

    // Helper method to access received transactions
    public static List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }

    // Helper method to clear the stored transactions (for test reset)
    public static void clearReceivedTransactions() {
        receivedTransactions.clear();
    }
}

