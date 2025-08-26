package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionProcessor {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionProcessor(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void process(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Skip if either user not found
        if (sender == null || recipient == null) return;

        // Skip if sender cannot cover the amount
        if (sender.getBalance().compareTo(transaction.getAmount()) < 0) return;

        // Use the incentive directly from the transaction
        java.math.BigDecimal incentive = transaction.getIncentive() != null
                ? transaction.getIncentive()
                : java.math.BigDecimal.ZERO;

        // Update balances
        sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));
        recipient.setBalance(recipient.getBalance().add(transaction.getAmount()).add(incentive));

        // Save users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive);
        transactionRepository.save(record);
    }
}
