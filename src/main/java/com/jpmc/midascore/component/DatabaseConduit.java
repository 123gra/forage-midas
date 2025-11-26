package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private UserRecord recipient;

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    // Requires isValidTransaction = true
    public void save(Transaction transaction) {
        UserRecord sender = getUser(transaction.getSenderId());
        UserRecord recipient = getUser(transaction.getRecipientId());
        float amount = transaction.getAmount();
        float incentive = transaction.getIncentive();

        // update and save - User Balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
        save(sender);
        save(recipient);

        //create and save - Transaction Record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentive);
        transactionRecordRepository.save(transactionRecord);
    }

    public boolean isValidTransaction(Transaction transaction) {
        UserRecord sender = getUser(transaction.getSenderId());
        UserRecord recipient = getUser(transaction.getRecipientId());
        float amount = transaction.getAmount();

        if (sender == null || recipient == null) return false;

        return sender.getBalance() >= transaction.getAmount();
    }

    //findById(id) returns Optional<UserRecord>
    public UserRecord getUser(long id) {
        return userRepository.findById(id).orElse(null);
    }

}
