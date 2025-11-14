package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskFourTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFourTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository; // Repository to fetch user balances

    @Test
    void task_four_verifier() throws InterruptedException {
        // Populate users
        userPopulator.populate();

        // Load transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");

        // Send transactions through Kafka
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // Wait for consumer to process messages
        Thread.sleep(2000);

        // Fetch Wilbur's balance
        User wilbur = userRepository.findByName("Wilbur"); // Adjust according to your User entity
        if (wilbur != null) {
            logger.info("Wilbur's balance after all transactions: {}", wilbur.getBalance());
        } else {
            logger.error("Wilbur not found!");
        }
    }
}

