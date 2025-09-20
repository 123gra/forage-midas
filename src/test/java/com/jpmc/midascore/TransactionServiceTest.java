package com.jpmc.midascore;

import com.jpmc.midascore.entity.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest // Tells Spring to load the full application context for this test
@Transactional  // Ensures that any database changes are rolled back after the test runs
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test // Marks this method as a test case
    void testRecordTransaction() {
        // --- 1. Arrange ---
        String accountId = "test-account-123";
        BigDecimal amount = new BigDecimal("100.50");

        // --- 2. Act ---
        // Call the method we want to test
        transactionService.recordTransaction(accountId, amount);

        // --- 3. Assert ---
        // Check the database to see if the transaction was saved
        List<Transaction> transactions = transactionRepository.findAll();
        Transaction savedTransaction = transactions.get(0);

        // Verify the results
        assertEquals(1, transactions.size()); // We should find exactly one transaction
        assertNotNull(savedTransaction.getId()); // The ID should be generated
        assertEquals(accountId, savedTransaction.getAccountId()); // The account ID should match
        assertEquals(0, amount.compareTo(savedTransaction.getAmount())); // The amount should match
        assertNotNull(savedTransaction.getTimestamp()); // The timestamp should be set
    }
}