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

/**
 * 🧪 TaskFourTests validates transaction processing via Kafka and direct invocation.
 * It populates users, sends transactions, and inspects final balances.
 */
@SpringBootTest
@DirtiesContext // Ensures a fresh Spring context for each test run
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
public class TaskFourTests
{
    // 📝 Logger for structured output and debugging
    static final Logger logger = LoggerFactory.getLogger(TaskFourTests.class);

    // 🔧 Injecting required components for the test flow
    @Autowired private TransactionProcessor transactionProcessor;
    @Autowired private KafkaProducer kafkaProducer;
    @Autowired private UserPopulator userPopulator;
    @Autowired private FileLoader fileLoader;
    @Autowired private UserRepository userRepository;

    /**
     * ✅ task_four_verifier simulates the full transaction pipeline:
     * 1. Populates user data
     * 2. Sends transactions via Kafka
     * 3. Processes transactions directly
     * 4. Verifies Wilbur's final balance
     * 5. Enters a debug loop for manual inspection
     */
    @Test
    void task_four_verifier() throws InterruptedException
    {
        // Step 1: Populate initial user data
        userPopulator.populate();

        // Step 2: Load transaction lines from test file
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");

        // Step 3: Send each transaction to Kafka
        for (String transactionLine : transactionLines)
        {
            kafkaProducer.send(transactionLine);
        }

        // Step 4: Allow time for async Kafka processing
        Thread.sleep(3000);

        // Step 5: Process transactions directly (bypassing Kafka)
        for (String transactionLine : transactionLines)
        {
            String[] parts = transactionLine.split(",\\s*");
            long senderId = Long.parseLong(parts[0]);
            long recipientId = Long.parseLong(parts[1]);
            java.math.BigDecimal amount = new java.math.BigDecimal(parts[2]);

            Transaction transaction = new Transaction(senderId, recipientId, amount);
            transactionProcessor.process(transaction);
        }

        // Step 6: Inspect Wilbur's final balance
        UserRecord wilbur = userRepository.findByName("wilbur");
        if (wilbur != null)
        {
            System.out.println("🎯 Wilbur's FINAL balance = " + wilbur.getBalance());
        }
        else
        {
            System.err.println("Wilbur not found in DB!");
        }

        // Step 7: Debug loop for manual inspection
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("use your debugger to find out what wilbur's balance is after all transactions are processed");
        logger.info("kill this test once you find the answer");

        while (true)
        {
            Thread.sleep(20000);
            logger.info("...");
        }
    }
}