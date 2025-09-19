package com.jpmc.midascore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate; // <--- NEW IMPORT

@Component
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    // Dependencies injected via constructor
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate; // <--- NEW FIELD

    @Autowired
    public TransactionListener(
            TransactionRepository transactionRepository,
            ObjectMapper objectMapper,
            RestTemplate restTemplate) { // <--- RestTemplate added to constructor

        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate; // <--- FIELD ASSIGNMENT
    }

    @KafkaListener(
            topics = "midas-transactions-in",
            groupId = "midas-core-group"
    )
    public void receiveTransaction(String message) {
        try {
            // 1. Deserialize and Save the Transaction (from Task 4)
            Transaction transaction = objectMapper.readValue(message, Transaction.class);
            transactionRepository.save(transaction);

            logger.info("Transaction saved with ID: {}", transaction.getId());

            // 2. REST CALL LOGIC (To be completed in the next step, currently just skeleton code)
            String incentiveUrl = "http://localhost:8080/api/incentive";

            // This is just placeholder logic for the next step
            // It uses the restTemplate we just injected.
            restTemplate.postForEntity(
                    incentiveUrl,
                    transaction,
                    String.class
            );

            logger.info("Transaction ID {} successfully submitted for incentive.", transaction.getId());


        } catch (JsonProcessingException e) {
            logger.error("Error deserializing message: {}", message, e);
        } catch (Exception e) {
            // Catch any exception, including RestTemplate exceptions
            logger.error("Error processing or incentivizing transaction: {}", e.getMessage());
        }
    }
}