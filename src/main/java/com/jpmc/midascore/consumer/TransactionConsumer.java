package com.jpmc.midascore.consumer; // Adjust package if needed

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.dto.TransactionDto;
// Import TransactionService if you need it later, but not required for this specific task goal.
// import com.jpmc.midascore.service.TransactionService; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionConsumer.class);

    // Inject the ObjectMapper configured in MidasCoreApplication
    @Autowired 
    private ObjectMapper objectMapper; 

    // Inject TransactionService if you were processing, e.g.:
    // @Autowired
    // private TransactionService transactionService;

    // Listen to the topic defined in application.properties/yml
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listenForTransactions(String message) {
        LOG.info("Kafka Listener: Received message."); // Basic log

        try {
            // Deserialize the JSON string message into your DTO
            TransactionDto transactionDto = objectMapper.readValue(message, TransactionDto.class);

            // --- BREAKPOINT LOCATION ---
            // Set your breakpoint on the line below to inspect the DTO
            LOG.info("Deserialized transaction: Account={}, Amount={}, Time={}", 
                     transactionDto.accountId(), 
                     transactionDto.amount(), // This is the value you need
                     transactionDto.timestamp());

            // For this task, we don't need to save yet.
            // In a later task, you would call:
            // transactionService.saveTransaction(transactionDto);

        } catch (Exception e) {
            LOG.error("Failed to process Kafka message: {}", message, e);
        }
    }
}