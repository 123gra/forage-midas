package com.jpmc.midascore;

// Task 3
// Task 4

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class TransactionProcessor {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;

    @Autowired
    public TransactionProcessor(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public void process(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Skip if either user not found
        if (sender == null || recipient == null) return;

        // Skip if sender cannot cover the amount
        if (sender.getBalance().compareTo(transaction.getAmount()) < 0) return;

        Incentive incentiveResponse = incentiveService.fetchIncentive(transaction);

// Take incentive amount from API response (fallback = 0 if null)
        java.math.BigDecimal incentive = (incentiveResponse != null && incentiveResponse.getAmount() != null)
                ? incentiveResponse.getAmount()
                : java.math.BigDecimal.ZERO;

        // Deduct from sender
        sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));

// Credit recipient including API incentive
        recipient.setBalance(recipient.getBalance().add(transaction.getAmount()).add(incentive));

        // Save users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive);
        transactionRepository.save(record);

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
