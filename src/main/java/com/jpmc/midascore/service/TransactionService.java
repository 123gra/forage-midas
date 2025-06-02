package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Validate transaction
        boolean isValid = validateTransaction(sender, recipient, transaction.getAmount());

        // Create transaction record
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), isValid);
        transactionRepository.save(record);

        // If valid, update balances
        if (isValid) {
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());
            userRepository.save(sender);
            userRepository.save(recipient);
        }
    }

    private boolean validateTransaction(UserRecord sender, UserRecord recipient, float amount) {
        // Check if sender and recipient exist
        if (sender == null || recipient == null) {
            return false;
        }

        // Check if sender has sufficient balance
        return sender.getBalance() >= amount;
    }

    public void printAllBalances() {
        Iterable<UserRecord> users = userRepository.findAll();
        System.out.println("\nCurrent Balances:");
        System.out.println("----------------");
        for (UserRecord user : users) {
            System.out.printf("%s: %.2f%n", user.getName(), user.getBalance());
        }
        System.out.println("----------------\n");
    }
} 