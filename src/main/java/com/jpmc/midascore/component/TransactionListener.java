package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListener {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void consume(Transaction transaction) {
        System.out.println("✅ Received Transaction: " + transaction);
        // Set a breakpoint here in debugger to watch first 4 amounts
    }
}
