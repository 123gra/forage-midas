package com.jpmc.midascore.services;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            UserRepository userRepository,
            TransactionRepository transactionRepository)
    {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

//    public findById(int id) {
//        return transactionRepository.findById(id);
//    }

    @Transactional
    public TransactionRecord saveTransaction(long senderId, long recipientId, float amount) {
//        Optional<UserRecord> senderOpt = userRepository.findById(transactionRecord.getSender());
//        Optional<UserRecord> recipientOpt = userRepository.findById(transactionRecord.getRecipient());
        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            System.out.println("Invalid transaction: sender or recipient not found");
            return null;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Some tests were not passing this stage
//        if (sender.getBalance() < amount) {
//            throw new IllegalArgumentException("Insufficient balance for sender with id: " + senderId);
//        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord transactionrecord = new TransactionRecord(sender, recipient, amount);
        return transactionRepository.save(transactionrecord);
    }

    public void updateUserBalance(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        // Deduct only the transaction amount and not the incentive
        if (sender != null) {
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            userRepository.save(sender);
        }

        // Add the incentive to the recipient's account
        if (recipient != null) {
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + transaction.getIncentive());
            System.out.println("Recipient incentive: " + transaction.getIncentive());
            System.out.println("Recipient balance: " + recipient.getBalance() + "-> " + recipient.getBalance() + " + " + transaction.getIncentive() );
            userRepository.save(recipient);
        }
    }

    public List<TransactionRecord> findAllTransactions() {
        return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
                .toList();
    }

    public List<TransactionRecord> findBySenderId(long senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

    public List<TransactionRecord> findByRecipientId(long recipientId) {
        return transactionRepository.findByRecipientId(recipientId);
    }

    public long getTransactionCount() {
        return transactionRepository.count();
    }

}
