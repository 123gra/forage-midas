package com.jpmc.midascore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @KafkaListener(topics = "transactions", groupId = "midas")
    public void handleTransaction(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            Transaction txn = mapper.readValue(message, Transaction.class);

            Optional<UserRecord> sendOpt = Optional.ofNullable(userRepository.findById(txn.getSenderId()));
            Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(txn.getRecipientId()));

            if(sendOpt.isEmpty() || recipientOpt.isEmpty()) return;

            UserRecord sender = sendOpt.get();
            UserRecord recipient = recipientOpt.get();

            if(sender.getBalance() >= txn.getAmount()){
                //update balances
                sender.setBalance(sender.getBalance() - txn.getAmount());
                recipient.setBalance(recipient.getBalance() + txn.getAmount());

                userRepository.save(sender);
                userRepository.save(recipient);

                //create transaction record
                TransactionRecord transactionRecord = new TransactionRecord();
                transactionRecord.setRecipient(recipient);
                transactionRecord.setUser(sender);
                transactionRecord.setAmount(txn.getAmount());

                transactionRecordRepository.save(transactionRecord);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
