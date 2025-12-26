package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.jpmc.midascore.foundation.Transaction;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
class TaskTwoTests {

    static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FileLoader fileLoader;

    @Test
    void task_two_verifier() throws InterruptedException {

        // Load lines from file
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        // Convert each line to Transaction and send
        for (String line : transactionLines) {
            String[] parts = line.split(",");
            
            // Transaction constructor expects: (long senderId, long recipientId, float amount)
            // Use .trim() to remove any spaces
            Transaction transaction = new Transaction(
                    Long.parseLong(parts[0].trim()),      // senderId as long (trim spaces)
                    Long.parseLong(parts[1].trim()),      // recipientId as long (trim spaces)
                    Float.parseFloat(parts[2].trim())     // amount as float (trim spaces)
            );

            kafkaProducer.send(transaction);
        }

        Thread.sleep(2000);

        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("use your debugger to watch for incoming transactions");
        logger.info("kill this test once you find the answer");

        while (true) {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}