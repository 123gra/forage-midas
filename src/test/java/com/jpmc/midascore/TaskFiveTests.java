package com.jpmc.midascore;

// 📦 Importing required testing, logging, and Spring components
import com.jpmc.midascore.foundation.Balance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * 🧪 TaskFiveTests validates end-to-end Kafka transaction processing.
 * It populates users, sends transactions, and verifies resulting balances.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=33400"
)
@DirtiesContext // Ensures fresh Spring context for each test run
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
public class TaskFiveTests
{
    // 📝 Logger for structured output
    static final Logger logger = LoggerFactory.getLogger(TaskFiveTests.class);

    // 🔧 Injecting required components for the test flow
    @Autowired private KafkaProducer kafkaProducer;
    @Autowired private UserPopulator userPopulator;
    @Autowired private FileLoader fileLoader;
    @Autowired private BalanceQuerier balanceQuerier;

    /**
     * ✅ task_five_verifier simulates the full transaction pipeline:
     * 1. Populates user data
     * 2. Loads transaction lines from file
     * 3. Sends transactions to Kafka
     * 4. Waits for processing
     * 5. Queries and logs final balances
     */
    @Test
    void task_five_verifier() throws InterruptedException
    {
        // Step 1: Populate initial user data
        userPopulator.populate();

        // Step 2: Load transaction lines from test file
        String[] transactionLines = fileLoader.loadStrings("/test_data/rueiwoqp.tyruei");

        // Step 3: Send each transaction to Kafka
        for (String transactionLine : transactionLines)
        {
            kafkaProducer.send(transactionLine);
        }

        // Step 4: Allow time for async processing
        Thread.sleep(2000);

        // Step 5: Log final balances for verification
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("submit the following output to complete the task (include begin and end output denotations)");

        StringBuilder output = new StringBuilder("\n").append("---begin output ---").append("\n");
        for (int i = 0; i < 13; i++)
        {
            Balance balance = balanceQuerier.query((long) i);
            output.append(balance.toString()).append("\n");
        }
        output.append("---end output ---");
        logger.info(output.toString());
    }
}