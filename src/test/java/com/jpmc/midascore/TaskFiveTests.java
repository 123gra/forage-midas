package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.jpmc.midascore.component.BalanceQuerier;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskFiveTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFiveTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private BalanceQuerier balanceQuerier;

    @Test
    void task_five_verifier() throws InterruptedException {
        // Populate initial user data
        userPopulator.populate();
        
        // Use the ACTUAL filename from test_data folder
        String[] transactionLines = fileLoader.loadStrings("/test_data/rueiwoqp.tyruei");
        
        // Parse each line and send as Transaction object
        for (String line : transactionLines) {
            String[] parts = line.split(",");
            
            Transaction transaction = new Transaction(
                    Long.parseLong(parts[0].trim()),
                    Long.parseLong(parts[1].trim()),
                    Float.parseFloat(parts[2].trim())
            );
            
            kafkaProducer.send(transaction);
        }
        
        Thread.sleep(2000);

        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("submit the following output to complete the task (include begin and end output denotations)");
        
        StringBuilder output = new StringBuilder("\n").append("---begin output ---").append("\n");
        for (int i = 0; i < 13; i++) {
            Balance balance = balanceQuerier.query((long) i);
            output.append(balance.toString()).append("\n");
        }
        output.append("---end output ---");
        
        logger.info(output.toString());
    }
}
