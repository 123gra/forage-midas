package com.jpmc.midascore;

// 📦 Importing required components for testing, logging, and Kafka simulation
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * 🧪 TaskTwoTests validates Kafka transaction publishing.
 * It loads transaction data and sends it to the Kafka topic for downstream processing.
 */
@SpringBootTest
@DirtiesContext // Ensures a fresh Spring context for each test run
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
class TaskTwoTests
{
    // 📝 Logger for structured output and debugging
    static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    // 🔧 Injecting required components for the test flow
    @Autowired private KafkaProducer kafkaProducer;
    @Autowired private FileLoader fileLoader;

    /**
     * ✅ task_two_verifier simulates Kafka publishing:
     * 1. Loads transaction lines from a test file
     * 2. Sends each line to Kafka
     * 3. Enters a debug loop for manual inspection
     */
    @Test
    void task_two_verifier() throws InterruptedException
    {
        // Step 1: Load transaction lines from test file
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        // Step 2: Send each transaction to Kafka
        for (String transactionLine : transactionLines)
        {
            kafkaProducer.send(transactionLine);
        }

        // Step 3: Allow time for async processing
        Thread.sleep(2000);

        // Step 4: Debug loop for manual inspection
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("use your debugger to watch for incoming transactions");
        logger.info("kill this test once you find the answer");

        while (true)
        {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}