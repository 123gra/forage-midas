package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    @KafkaListener(topics = "transaction")
    public void processMessage(Transaction transaction) {
        // ...
    }
}
