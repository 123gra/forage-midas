package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    
    private final List<Float> firstFourAmounts = Collections.synchronizedList(new ArrayList<>());
    private int transactionCount = 0;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void receiveTransaction(Transaction transaction) {
        transactionCount++;
        logger.info("Received transaction #{}: {}", transactionCount, transaction);
        
        // Capture first 4 transaction amounts for debugging
        if (transactionCount <= 4) {
            firstFourAmounts.add(transaction.getAmount());
            logger.info("Transaction #{} amount: {}", transactionCount, transaction.getAmount());
        }
        
        // For now, just log the transaction - processing will come later
    }
    
    public List<Float> getFirstFourAmounts() {
        return new ArrayList<>(firstFourAmounts);
    }
}

