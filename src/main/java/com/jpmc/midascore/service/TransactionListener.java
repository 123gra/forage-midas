package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepo;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepo transactionRecordRepo;

    @Transactional
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas")
    public void handleTransaction(Transaction txn) {
        Optional<UserRecord> sendOpt = Optional.ofNullable(userRepository.findById(txn.getSenderId()));
        Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(txn.getRecipientId()));

        if (sendOpt.isEmpty() || recipientOpt.isEmpty()) return;

        UserRecord sender = sendOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() >= txn.getAmount()) {
            // update balances
            sender.setBalance(sender.getBalance() - txn.getAmount());
            recipient.setBalance(recipient.getBalance() + txn.getAmount());

            userRepository.save(sender);
            userRepository.save(recipient);

            // create transaction record
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setRecipient(recipient);
            transactionRecord.setUser(sender);
            transactionRecord.setAmount(txn.getAmount());

            transactionRecordRepo.save(transactionRecord);
        }
    }
}
