package com.jpmc.midascore;

// 📦 Importing required components for testing, logging, and data access
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import java.math.BigDecimal;

/**
 * 🧪 TaskThreeTests validates transaction processing via Kafka and direct invocation.
 * It populates users, sends transactions, and inspects Waldorf's final balance.
 */
@SpringBootTest
@DirtiesContext // Ensures a fresh Spring context for each test run
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
public class TaskThreeTests
{
    // 📝 Logger for structured output and debugging
    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    // 🔧 Injecting required components for the test flow
    @Autowired private TransactionProcessor transactionProcessor;
    @Autowired private KafkaProducer kafkaProducer;
    @Autowired private UserPopulator userPopulator;
    @Autowired private FileLoader fileLoader;
    @Autowired private UserRepository userRepository;

    /**
     * ✅ task_three_verifier simulates the full transaction pipeline:
     * 1. Populates user data
     * 2. Sends transactions via Kafka
     * 3. Processes transactions directly
     * 4. Verifies Waldorf's final balance
     * 5. Enters a debug loop for manual inspection
     */
    @Test
    void task_three_verifier() throws InterruptedException
    {
        // Step 1: Populate initial user data
        userPopulator.populate();

        // Step 2: Load transaction lines from test file
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");

        // Step 3: Send each transaction to Kafka
        for (String transactionLine : transactionLines)
        {
            kafkaProducer.send(transactionLine);
        }

        // Step 4: Allow time for async Kafka processing
        Thread.sleep(2000);

        // Step 5: Process transactions directly (bypassing Kafka)
        for (String line : transactionLines)
        {
            String[] parts = line.split(",");
            long senderId = Long.parseLong(parts[0].trim());
            long recipientId = Long.parseLong(parts[1].trim());
            BigDecimal amount = new BigDecimal(parts[2].trim());

            Transaction transaction = new Transaction(senderId, recipientId, amount);
            transactionProcessor.process(transaction);
        }

        // Step 6: Inspect Waldorf's final balance
        UserRecord waldorf = userRepository.findByName("waldorf");
        if (waldorf != null)
        {
            int finalBalance = waldorf.getBalance()
                    .setScale(0, BigDecimal.ROUND_DOWN)
                    .intValue();
            System.out.println("🔥 Waldorf's FINAL balance (rounded down): " + finalBalance);
        }

        // Step 7: Debug loop for manual inspection
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("use your debugger to find out what waldorf's balance is after all transactions are processed");
        logger.info("kill this test once you find the answer");

        while (true)
        {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}