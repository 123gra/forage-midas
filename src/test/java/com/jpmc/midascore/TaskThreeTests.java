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
public class TaskThreeTests {
    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository; // Add your repository or service to fetch balances

    @Test
    void task_three_verifier() throws InterruptedException {
        // Step 1: Populate users
        userPopulator.populate();

        // Step 2: Load transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");

        // Step 3: Send transactions through Kafka
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // Step 4: Wait for the consumer to process messages
        Thread.sleep(2000); // You can increase if needed

        // Step 5: Fetch Waldorf's balance
        User waldorf = userRepository.findByName("Waldorf"); // Adjust based on your User entity
        if (waldorf != null) {
            logger.info("Waldorf's balance after all transactions: {}", waldorf.getBalance());
        } else {
            logger.error("Waldorf not found!");
        }
    }
}

