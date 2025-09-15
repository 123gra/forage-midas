package com.jpmc.midascore;

// 📦 Importing required components for data access, transaction management, and business logic
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

/**
 * 🔄 TransactionProcessor handles the core logic for processing financial transactions.
 * It validates users, applies incentives, updates balances, and records the transaction.
 */
@Component
public class TransactionProcessor
{
    // 🧩 Injected dependencies for user data, transaction logging, and incentive calculation
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;

    /**
     * 🛠️ Constructor-based injection for better testability and immutability.
     */
    @Autowired
    public TransactionProcessor(UserRepository userRepository,
                                TransactionRepository transactionRepository,
                                IncentiveService incentiveService)
    {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    /**
     * 💳 Processes a transaction between two users:
     * - Validates sender and recipient
     * - Checks sender's balance
     * - Fetches incentive from external service
     * - Updates balances
     * - Persists changes and logs the transaction
     *
     * @param transaction the transaction to process
     */
    @Transactional
    public void process(Transaction transaction)
    {
        // Step 1: Retrieve sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Step 2: Validate users
        if (sender == null || recipient == null) return;

        // Step 3: Validate sender's balance
        if (sender.getBalance().compareTo(transaction.getAmount()) < 0) return;

        // Step 4: Fetch incentive from external service
        Incentive incentiveResponse = incentiveService.fetchIncentive(transaction);
        BigDecimal incentive = (incentiveResponse != null && incentiveResponse.getAmount() != null)
                ? incentiveResponse.getAmount()
                : BigDecimal.ZERO;

        // Step 5: Update balances
        sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));
        recipient.setBalance(recipient.getBalance().add(transaction.getAmount()).add(incentive));

        // Step 6: Persist updated user records
        userRepository.save(sender);
        userRepository.save(recipient);

        // Step 7: Record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive);
        transactionRepository.save(record);

        // Step 8: Log transaction summary
        System.out.printf(
                "Processed: Sender=%s, Recipient=%s, Amount=%s, Incentive=%s, SenderBalance=%s, RecipientBalance=%s%n",
                sender.getName(),
                recipient.getName(),
                transaction.getAmount().toPlainString(),
                incentive.toPlainString(),
                sender.getBalance().toPlainString(),
                recipient.getBalance().toPlainString()
        );
    }
}