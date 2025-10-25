package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
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
        for (String transactionLine : transactionLines) {
            String[] transactionData = transactionLine.split(", ");
            kafkaProducer.send(new Transaction(Long.parseLong(transactionData[0]), Long.parseLong(transactionData[1]), Float.parseFloat(transactionData[2])));
        }
        Thread.sleep(5000); // Give Kafka and API some time to process messages

        // Query for "wilbur" user's balance
        UserRecord wilburUser = userRepository.findByName("wilbur").orElse(null);
        if (wilburUser != null) {
            logger.info("Wilbur's balance after transactions: {}", wilburUser.getBalance().toPlainString());
        } else {
            logger.info("Wilbur user not found.");
        }
        Thread.sleep(5000); // Give time for logs to flush
    }
}
