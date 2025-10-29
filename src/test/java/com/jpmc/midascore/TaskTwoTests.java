package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import com.jpmc.midascore.model.Transaction;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TaskTwoTests {

    static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private KafkaConsumer kafkaConsumer; 

    @Test
    void task_two_verifier() throws InterruptedException {

        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        
        Thread.sleep(3000);

        
        List<Transaction> transactions = kafkaConsumer.getReceivedTransactions();

        
        logger.info("----------------------------------------------------------");
        logger.info("First Four Transactions Received by Midas Core:");
        for (int i = 0; i < Math.min(4, transactions.size()); i++) {
            logger.info("Transaction {} -> {}", i + 1, transactions.get(i));
        }
        logger.info("----------------------------------------------------------");

        
        logger.info("Use your debugger to inspect 'transactions' if needed.");
        Thread.sleep(10000); 
    }
}
