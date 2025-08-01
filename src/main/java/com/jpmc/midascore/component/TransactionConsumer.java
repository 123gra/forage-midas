package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.exception.ValidationException;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionConsumer {

    private final UserRepository userRepository;

    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionConsumer(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(Transaction transaction) {
        try {
            final UserRecord receiver = validateAndGetReceiver(transaction);
            final UserRecord sender = validateAndGetSender(transaction);
            final TransactionRecord transactionRecord = new TransactionRecord(sender, receiver,
                    transaction.getAmount);
            updateDatabase(transactionRecord);
            System.out.println();
        } catch (ValidationException e) {
            System.out.println("no modification to db because of error: " + e.getMessage());
        }
    }

    @Transactional
    private void updateDatabase(final TransactionRecord transactionRecord) {
        transactionRecordRepository.save(transactionRecord);
    }

    private UserRecord validateAndGetReceiver(final Transaction transaction) throws ValidationException {
        final Long recipientId = transaction.getRecipientId();
        Optional<UserRecord> userRecord = userRepository.findById(recipientId);
        if (!userRecord.isPresent()) {
            final String errorMsg = "Recipient not found with id " + recipientId;
            throw new ValidationException(errorMsg);
        }
        return userRecord.get();
    }

    private UserRecord validateAndGetSender(final Transaction transaction) throws ValidationException {
        final Long senderId = transaction.getSenderId();
        Optional<UserRecord> userRecord = userRepository.findById(senderId);
        if (!userRecord.isPresent()) {
            final String errorMsg = "Sender not found with id " + senderId;
            throw new ValidationException(errorMsg);
        }
        final UserRecord sender = userRecord.get();
        if (sender.getBalance() < transaction.getAmount()) {
            final String errorMsg = "Sender does not have enough balance to complete the transaction";
            throw new ValidationException(errorMsg);
        }
        return sender;
    }
}