package com.jpmc.midascore;

import com.jpmc.midascore.component.BalanceQuerier;
import com.jpmc.midascore.component.KafkaProducer;
import com.jpmc.midascore.entity.UserRecord;
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
public class TaskThreeTests {
    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;
    
    @Autowired
    private BalanceQuerier balanceQuerier;

    @Test
    void task_three_verifier() throws InterruptedException {
        userPopulator.populate();
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        Thread.sleep(2000);

        // Query Waldorf's balance
        UserRecord waldorf = balanceQuerier.queryUserByName("waldorf");
        if (waldorf != null) {
            int waldorfBalance = (int) Math.floor(waldorf.getBalance());
            logger.info("----------------------------------------------------------");
            logger.info("----------------------------------------------------------");
            logger.info("----------------------------------------------------------");
            logger.info("Waldorf's balance after all transactions: {}", waldorfBalance);
            logger.info("Answer: {}", waldorfBalance);
        } else {
            logger.error("Could not find Waldorf user");
        }
    }
}
