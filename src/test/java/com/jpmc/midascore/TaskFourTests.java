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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
    private UserRepository userRepository;

    @Test
    void task_four_verifier() throws InterruptedException {
        logger.info("========================================");
        logger.info("🚀 Starting TaskFourTests...");
        logger.info("========================================");
        
        // Populate initial user data
        userPopulator.populate();
        
        // Load transaction data from file
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");
        
        logger.info("📤 Sending {} transactions to Kafka...", transactionLines.length);
        
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
        logger.info("⏳ Waiting 5 seconds for all transactions to process...");
        Thread.sleep(5000);

        // 🎯 FIND WILBUR'S BALANCE
        UserRecord wilbur = userRepository.findByName("wilbur");
        if (wilbur != null) {
            int wilburBalance = (int) Math.floor(wilbur.getBalance());
            
            logger.info("========================================");
            logger.info("========================================");
            logger.info("🎯 WILBUR'S FINAL BALANCE: {}", wilburBalance);
            logger.info("Expected: 3729 (with 2% incentives)");
            logger.info("========================================");
            logger.info("========================================");
            
            if (wilburBalance == 3729) {
                logger.info("✅ SUCCESS! Balance matches expected value!");
            } else if (wilburBalance == 3656) {
                logger.error("❌ FAIL! Balance is 3656 - incentives are NOT being applied!");
                logger.error("The web server might not be starting properly");
            } else {
                logger.warn("⚠️ Balance is {} - unexpected value", wilburBalance);
            }
        } else {
            logger.error("❌ ERROR: User 'wilbur' not found in database!");
        }
        
        logger.info("");
        logger.info("Use your debugger to verify the balance");
        logger.info("Kill this test once you have confirmed the answer");
        
        while (true) {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}