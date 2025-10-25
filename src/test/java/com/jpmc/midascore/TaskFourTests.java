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

    @Autowired  // ADD THIS LINE
    private TransactionService transactionService;

    @Test
    void task_four_verifier() throws InterruptedException {
        userPopulator.populate();
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        Thread.sleep(2000);

        // ADD THESE LINES TO GET WILBUR'S BALANCE
        var wilbur = transactionService.findUserByName("wilbur");
        if (wilbur != null) {
            double wilburBalance = wilbur.getBalance();
            double roundedAnswer = Math.floor(wilburBalance);

            System.out.println("==========================================");
            System.out.println("WILBUR'S BALANCE: " + wilburBalance);
            System.out.println("ANSWER TO SUBMIT: " + roundedAnswer);
            System.out.println("==========================================");

            logger.info("Wilbur's balance: {}", wilburBalance);
            logger.info("Rounded answer: {}", roundedAnswer);
        } else {
            System.out.println("Wilbur user not found!");
        }
    }
}
        // Remove the infinite loop - just let the test complete
        // The answer will be printed above}