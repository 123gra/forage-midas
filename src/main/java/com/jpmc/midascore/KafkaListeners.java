package com.jpmc.midascore;

import com.jpmc.midascore.controllers.UserController;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.services.IncentiveService;
import com.jpmc.midascore.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncentiveService incentiveService;

    @KafkaListener(
            topics = "midastopic",
            groupId = "groupId"
    )
    void listener(Transaction transaction) {
        System.out.println("Kafka Listener received: " + transaction);

        // Transaction is being instantiated from the TransactioRecord class
//        TransactionRecord transactionRecord = new TransactionRecord();
//        transactionRecord.setAmount(transaction.getAmount());
//
//        UserRecord sender = userRepository.findById(transaction.getSenderId())
//                .orElseThrow(() -> new IllegalStateException("Sender not found"));
//
//        UserRecord recipient = userRepository.findById(transaction.getRecipientId())
//                .orElseThrow(() -> new IllegalStateException("Recipient not found"));
//
//        transactionRecord.setSender(sender);
//        transactionRecord.setRecipient(recipient);

        TransactionRecord savedTransaction = transactionService.saveTransaction(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount()
        );
        System.out.println("Saved Transaction: " + savedTransaction.getSender().getBalance());

        float incentiveAmount = incentiveService.getIncentive(transaction.getIncentive());
        System.out.println("Incentive Amount: " + incentiveAmount);
        transaction.setIncentive(incentiveAmount);

        transactionService.updateUserBalance(transaction);
        System.out.println("New balance: " + transaction.toString());
    }
}
