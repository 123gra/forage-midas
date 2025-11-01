package com.jpmc.midascore;

import com.jpmc.midascore.repository.UserRepository;
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
    private UserRepository userRepository;

    @Test
    void task_four_verifier() throws InterruptedException {
        userPopulator.populate();
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        logger.info("Sending {} transactions...", transactionLines.length);
        
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        
        // Wait for all transactions to be processed
        logger.info("Waiting for transactions to be processed...");
        Thread.sleep(5000); // Increased wait time to ensure all transactions are processed

        // Set a breakpoint on the next line to inspect Wilbur's balance
        logger.info("----------------------------------------------------------");
        logger.info("BREAKPOINT HERE: All transactions should be processed now");
        logger.info("In debugger, evaluate: userRepository.findByName(\"wilbur\").getBalance()");
        logger.info("Or: userRepository.findById(9L).getBalance()");
        logger.info("Wilbur's balance : {}", 
                    Math.floor(userRepository.findByName("wilbur").getBalance()));
        logger.info("----------------------------------------------------------");
        
        // Keep test running so debugger can inspect
        while (true) {
            Thread.sleep(20000);
            logger.info("Test still running...");
        }
    }
}
