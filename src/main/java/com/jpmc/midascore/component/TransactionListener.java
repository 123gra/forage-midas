package com.jpmc.midascore.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

public class TransactionListener {
   @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(String message) throws JsonProcessingException {
        Transaction transaction = new ObjectMapper().readValue(message, Transaction.class);
        System.out.println("Received transaction: " + transaction);
    }
    
}
