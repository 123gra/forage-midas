package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveService incentiveService;

    public DatabaseConduit(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveService = incentiveService;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord findUserByName(String name) {
        return userRepository.findByName(name);
    }

    public UserRecord findUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public boolean processTransaction(long senderId, long recipientId, float amount) {
        // Validate sender exists
        UserRecord sender = userRepository.findById(senderId);
        if (sender == null) {
            return false;
        }

        // Validate recipient exists
        UserRecord recipient = userRepository.findById(recipientId);
        if (recipient == null) {
            return false;
        }

        // Validate sender has sufficient balance
        if (sender.getBalance() < amount) {
            return false;
        }

        // Call incentive API to get incentive amount
        Transaction transaction = new Transaction(senderId, recipientId, amount);
        Incentive incentive = incentiveService.getIncentive(transaction);
        float incentiveAmount = incentive.getAmount();

        // Update balances
        // Deduct transaction amount from sender
        sender.setBalance(sender.getBalance() - amount);
        // Add transaction amount + incentive to recipient
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record with incentive
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRecordRepository.save(transactionRecord);

        return true;
    }
}
