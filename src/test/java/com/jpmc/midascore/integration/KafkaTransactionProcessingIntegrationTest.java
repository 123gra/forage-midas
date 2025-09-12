package com.jpmc.midascore.integration;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.TransactionStatus;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for Kafka transaction processing
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.group-id=test-integration-group"
})
public class KafkaTransactionProcessingIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseConduit databaseConduit;

    @Value("${general.kafka-topic}")
    private String topic;

    private UserRecord sender;
    private UserRecord recipient;

    @BeforeEach
    public void setUp() {
        // Clean up database
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        sender = new UserRecord("Test Sender", 1000.0f);
        recipient = new UserRecord("Test Recipient", 500.0f);
        
        sender = databaseConduit.saveUser(sender);
        recipient = databaseConduit.saveUser(recipient);
    }

    @Test
    public void testSuccessfulTransactionProcessing() {
        // Create a valid transaction
        Transaction transaction = new Transaction(sender.getId(), recipient.getId(), 100.0f);

        // Send transaction via Kafka
        kafkaTemplate.send(topic, transaction);

        // Wait for async processing and verify the transaction was processed successfully
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var transactions = transactionRepository.findAll();
            assertFalse(transactions.isEmpty(), "Transaction should be saved to database");
            
            Transaction savedTransaction = transactions.iterator().next();
            assertEquals(TransactionStatus.COMPLETED, savedTransaction.getStatus());
            assertEquals(sender.getId(), savedTransaction.getSenderId());
            assertEquals(recipient.getId(), savedTransaction.getRecipientId());
            assertEquals(100.0f, savedTransaction.getAmount(), 0.01f);
        });

        // Verify balances were updated correctly
        UserRecord updatedSender = databaseConduit.findUserById(sender.getId()).orElse(null);
        UserRecord updatedRecipient = databaseConduit.findUserById(recipient.getId()).orElse(null);
        
        assertNotNull(updatedSender);
        assertNotNull(updatedRecipient);
        assertEquals(900.0f, updatedSender.getBalance(), 0.01f);
        assertEquals(600.0f, updatedRecipient.getBalance(), 0.01f);
    }

    @Test
    public void testInvalidSenderTransactionProcessing() {
        // Create transaction with invalid sender
        Transaction transaction = new Transaction(999L, recipient.getId(), 100.0f);

        // Send transaction via Kafka
        kafkaTemplate.send(topic, transaction);

        // Wait for async processing and verify the transaction failed
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var transactions = transactionRepository.findAll();
            assertFalse(transactions.isEmpty(), "Failed transaction should be saved to database");
            
            Transaction savedTransaction = transactions.iterator().next();
            assertEquals(TransactionStatus.FAILED, savedTransaction.getStatus());
            assertEquals(999L, savedTransaction.getSenderId());
        });

        // Verify balances were not changed
        UserRecord unchangedSender = databaseConduit.findUserById(sender.getId()).orElse(null);
        UserRecord unchangedRecipient = databaseConduit.findUserById(recipient.getId()).orElse(null);
        
        assertNotNull(unchangedSender);
        assertNotNull(unchangedRecipient);
        assertEquals(1000.0f, unchangedSender.getBalance(), 0.01f);
        assertEquals(500.0f, unchangedRecipient.getBalance(), 0.01f);
    }

    @Test
    public void testInvalidRecipientTransactionProcessing() {
        // Create transaction with invalid recipient
        Transaction transaction = new Transaction(sender.getId(), 999L, 100.0f);

        // Send transaction via Kafka
        kafkaTemplate.send(topic, transaction);

        // Wait for async processing and verify the transaction failed
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var transactions = transactionRepository.findAll();
            assertFalse(transactions.isEmpty(), "Failed transaction should be saved to database");
            
            Transaction savedTransaction = transactions.iterator().next();
            assertEquals(TransactionStatus.FAILED, savedTransaction.getStatus());
            assertEquals(999L, savedTransaction.getRecipientId());
        });

        // Verify balances were not changed
        UserRecord unchangedSender = databaseConduit.findUserById(sender.getId()).orElse(null);
        UserRecord unchangedRecipient = databaseConduit.findUserById(recipient.getId()).orElse(null);
        
        assertNotNull(unchangedSender);
        assertNotNull(unchangedRecipient);
        assertEquals(1000.0f, unchangedSender.getBalance(), 0.01f);
        assertEquals(500.0f, unchangedRecipient.getBalance(), 0.01f);
    }

    @Test
    public void testInsufficientBalanceTransactionProcessing() {
        // Create transaction with amount greater than sender's balance
        Transaction transaction = new Transaction(sender.getId(), recipient.getId(), 1500.0f);

        // Send transaction via Kafka
        kafkaTemplate.send(topic, transaction);

        // Wait for async processing and verify the transaction failed
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var transactions = transactionRepository.findAll();
            assertFalse(transactions.isEmpty(), "Failed transaction should be saved to database");
            
            Transaction savedTransaction = transactions.iterator().next();
            assertEquals(TransactionStatus.FAILED, savedTransaction.getStatus());
            assertEquals(1500.0f, savedTransaction.getAmount(), 0.01f);
        });

        // Verify balances were not changed
        UserRecord unchangedSender = databaseConduit.findUserById(sender.getId()).orElse(null);
        UserRecord unchangedRecipient = databaseConduit.findUserById(recipient.getId()).orElse(null);
        
        assertNotNull(unchangedSender);
        assertNotNull(unchangedRecipient);
        assertEquals(1000.0f, unchangedSender.getBalance(), 0.01f);
        assertEquals(500.0f, unchangedRecipient.getBalance(), 0.01f);
    }

    @Test
    public void testNegativeAmountTransactionProcessing() {
        // Create transaction with negative amount
        Transaction transaction = new Transaction(sender.getId(), recipient.getId(), -100.0f);

        // Send transaction via Kafka
        kafkaTemplate.send(topic, transaction);

        // Wait for async processing and verify the transaction failed
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var transactions = transactionRepository.findAll();
            assertFalse(transactions.isEmpty(), "Failed transaction should be saved to database");
            
            Transaction savedTransaction = transactions.iterator().next();
            assertEquals(TransactionStatus.FAILED, savedTransaction.getStatus());
            assertEquals(-100.0f, savedTransaction.getAmount(), 0.01f);
        });

        // Verify balances were not changed
        UserRecord unchangedSender = databaseConduit.findUserById(sender.getId()).orElse(null);
        UserRecord unchangedRecipient = databaseConduit.findUserById(recipient.getId()).orElse(null);
        
        assertNotNull(unchangedSender);
        assertNotNull(unchangedRecipient);
        assertEquals(1000.0f, unchangedSender.getBalance(), 0.01f);
        assertEquals(500.0f, unchangedRecipient.getBalance(), 0.01f);
    }

    @Test
    public void testSameSenderRecipientTransactionProcessing() {
        // Create transaction where sender and recipient are the same
        Transaction transaction = new Transaction(sender.getId(), sender.getId(), 100.0f);

        // Send transaction via Kafka
        kafkaTemplate.send(topic, transaction);

        // Wait for async processing and verify the transaction failed
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var transactions = transactionRepository.findAll();
            assertFalse(transactions.isEmpty(), "Failed transaction should be saved to database");
            
            Transaction savedTransaction = transactions.iterator().next();
            assertEquals(TransactionStatus.FAILED, savedTransaction.getStatus());
            assertEquals(sender.getId(), savedTransaction.getSenderId());
            assertEquals(sender.getId(), savedTransaction.getRecipientId());
        });

        // Verify balance was not changed
        UserRecord unchangedSender = databaseConduit.findUserById(sender.getId()).orElse(null);
        assertNotNull(unchangedSender);
        assertEquals(1000.0f, unchangedSender.getBalance(), 0.01f);
    }
}
