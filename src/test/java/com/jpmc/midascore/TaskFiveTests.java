package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Balance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskFiveTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFiveTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private BalanceQuerier balanceQuerier;

    @Test
    void task_five_verifier() throws InterruptedException {
        // Populate users
        userPopulator.populate();

        // Load transactions from file
        String[] transactionLines = fileLoader.loadStrings("/test_data/rueiwoqp.tyruei");

        // Send all transactions to Kafka
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // Wait for the consumer to process all messages
        Thread.sleep(2000);

        // Build output for submission
        StringBuilder output = new StringBuilder("\n---begin output---\n");
        for (int i = 0; i <= 12; i++) { // Query balances for IDs 0 through 12
            Balance balance = balanceQuerier.query((long) i);
            if (balance != null) {
                output.append(balance.toString()).append("\n");
            } else {
                output.append("User ID ").append(i).append(": balance not found\n");
            }
        }
        output.append("---end output---");

        // Print the final output
        logger.info(output.toString());
    }
}


