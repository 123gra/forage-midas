package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.service.UserService;
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
public class WilburBalanceTest {
    static final Logger logger = LoggerFactory.getLogger(WilburBalanceTest.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserService userService;

    @Test
    void findWilburBalance() throws InterruptedException {
        // Populate users
        userPopulator.populate();
        
        // Process transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        
        // Wait for processing
        Thread.sleep(5000);
        
        // Find wilbur user
        UserRecord wilbur = userService.findUserByName("wilbur");
        if (wilbur != null) {
            int balance = (int) Math.floor(wilbur.getBalance());
            logger.info("Wilbur's balance after all transactions: {}", balance);
            logger.info("Wilbur's exact balance: {}", wilbur.getBalance());
        } else {
            logger.error("Wilbur user not found!");
        }
    }
}
