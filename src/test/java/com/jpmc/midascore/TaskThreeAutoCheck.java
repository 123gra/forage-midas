package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskThreeAutoCheck {
    static final Logger logger = LoggerFactory.getLogger(TaskThreeAutoCheck.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository;

    @Test
    void run_and_check_waldorf_balance() throws Exception {
        // populate users
        userPopulator.populate();

        // send transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // wait a bit for consumer to process messages
        Thread.sleep(2500);

        // waldorf is the 5th user in the test data, so id should be 5
        UserRecord waldorf = userRepository.findById(5L);
        assertNotNull(waldorf, "waldorf user should exist");
        logger.info("WALDORF_BALANCE={} (raw)", waldorf.getBalance());
    }
}