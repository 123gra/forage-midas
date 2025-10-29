package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerComponent {

    public final String topic;
    private final DatabaseConduit databaseConduit;

    public KafkaListenerComponent(@Value("${general.kafka-topic}") String topic, DatabaseConduit databaseConduit) {
        this.topic = topic;
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "#{__listener.topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);
        databaseConduit.processTransaction(transaction);
    }
}
