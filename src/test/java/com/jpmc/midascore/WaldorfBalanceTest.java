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
public class WaldorfBalanceTest {
    static final Logger logger = LoggerFactory.getLogger(WaldorfBalanceTest.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserService userService;

    @Test
    void findWaldorfBalance() throws InterruptedException {
        // Populate users
        userPopulator.populate();
        
        // Process transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        
        // Wait for processing
        Thread.sleep(3000);
        
        // Find waldorf user
        UserRecord waldorf = userService.findUserByName("waldorf");
        if (waldorf != null) {
            int balance = (int) Math.floor(waldorf.getBalance());
            logger.info("Waldorf's balance after all transactions: {}", balance);
            logger.info("Waldorf's exact balance: {}", waldorf.getBalance());
        } else {
            logger.error("Waldorf user not found!");
        }
    }
}
