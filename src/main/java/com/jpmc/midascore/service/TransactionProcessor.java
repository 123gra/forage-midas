package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.UserRecord;
import com.jpmc.midascore.foundation.*;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TransactionProcessor {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionProcessor(UserRepository userRepository,
                                TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public void process(Transaction transaction) {

        Optional<UserRecord> senderOpt =
                userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt =
                userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        float senderBalance = sender.getBalance().getAmount();
        float amount = transaction.getAmount();

        if (senderBalance < amount) {
            return;
        }

        sender.getBalance().setAmount(senderBalance - amount);
        recipient.getBalance().setAmount(
                recipient.getBalance().getAmount() + amount
        );
    }

}
