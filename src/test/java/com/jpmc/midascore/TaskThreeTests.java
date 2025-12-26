package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

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
    private UserRepository userRepository;

    @Test
    void task_three_verifier() throws InterruptedException {
        // Populate initial user data
        userPopulator.populate();
        
        // Load transaction data from file
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        
        // Parse each line and send as Transaction object
        for (String line : transactionLines) {
            String[] parts = line.split(",");
            
            // Parse: senderId, recipientId, amount
            Transaction transaction = new Transaction(
                    Long.parseLong(parts[0].trim()),      // senderId as long
                    Long.parseLong(parts[1].trim()),      // recipientId as long
                    Float.parseFloat(parts[2].trim())     // amount as float
            );
            
            kafkaProducer.send(transaction);
        }
        
        // Wait for all transactions to be processed
        Thread.sleep(2000);

        // 🎯 FIND WALDORF'S BALANCE
        UserRecord waldorf = userRepository.findByName("waldorf");
        if (waldorf != null) {
            int waldorfBalance = (int) Math.floor(waldorf.getBalance());
            
            logger.info("========================================");
            logger.info("========================================");
            logger.info("🎯 WALDORF'S FINAL BALANCE: {}", waldorfBalance);
            logger.info("========================================");
            logger.info("========================================");
        } else {
            logger.error("User 'waldorf' not found in database!");
        }
        
        logger.info("use your debugger to find out what waldorf's balance is after all transactions are processed");
        logger.info("kill this test once you find the answer");
        
        while (true) {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}