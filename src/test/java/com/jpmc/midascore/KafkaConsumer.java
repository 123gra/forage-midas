package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.jpmc.midascore.TaskTwoTests.logger;

@Component
public class KafkaConsumer {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public KafkaConsumer(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }


    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        //validate Sender and Recipient
        if(sender == null || recipient == null){
            return;
        }
        //validate Balance
        if(sender.getBalance() < transaction.getAmount()){
            System.out.println("Insufficient Balance for User: " + sender.getName());
            System.out.println("recipient: " + recipient.getName());
            return;
        }

        //Update Balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());


        //Save updated Users
        userRepository.save(sender);
        userRepository.save(recipient);

        //Record Transaction
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(transaction.getAmount());
        transactionRecordRepository.save(record);
    }

}
