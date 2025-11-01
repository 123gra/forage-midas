package com.jpmc.midascore;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import com.jpmc.midascore.model.Transaction;

@Service
public class KafkaConsumer {

    private List<Transaction> receivedTransactions = new ArrayList<>();

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void consume(Transaction transaction) {
        receivedTransactions.add(transaction);
        System.out.println("Received transaction: " + transaction);
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }
}
