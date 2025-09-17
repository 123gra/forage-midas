package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TransactionRecordRepository transactionRepo;

    @Transactional
    public void processTransaction(Transaction tx) {
        Optional<UserRecord> senderOpt = userRepo.findById(tx.getSenderId());
        Optional<UserRecord> recipientOpt = userRepo.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < tx.getAmount()) return;

        // update balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount());

        // save transaction record
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount((double) tx.getAmount());
        record.setTimestamp(System.currentTimeMillis());

        transactionRepo.save(record);
        userRepo.save(sender);
        userRepo.save(recipient);
    }
}
