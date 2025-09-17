package com.jpmc.midascore;

import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    topics = { "test-topic" },
    brokerProperties = { "listeners=PLAINTEXT://localhost:9092" }
)
public class TaskThreeTests {

    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Test
    void task_three_verifier() throws InterruptedException {
        // Populate initial users
        userPopulator.populate();

        // Load transactions from test file
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");

        // Send transactions to Kafka
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // Ensure all messages are flushed to Kafka before checking balances
        kafkaProducer.flush();

        // Wait a bit for Kafka consumer to process messages
        Thread.sleep(2000);

        // Debugging info for waldorf's balance
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("Use your debugger to find out what waldorf's balance is after all transactions are processed.");
        logger.info("Kill this test once you find the answer.");

        // Infinite loop for manual debugging
        while (true) {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}
