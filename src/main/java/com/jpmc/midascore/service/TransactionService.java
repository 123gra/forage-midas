package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.models.Incentive;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import jdk.jfr.RecordingState;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}",
            groupId = "midas-core-group"
    )
    @Transactional
    public void processTransaction(Transaction transaction) {


        Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive",
                transaction,
                Incentive.class);

        if(incentive!=null){
            transaction.setIncentive(incentive.getAmount());
        }
        else transaction.setIncentive(0);
        UserRecord sender=userRepository.findById(transaction.getSenderId());
        UserRecord recipient=userRepository.findById(transaction.getRecipientId());

        if(sender==null || recipient==null){
            System.out.println("Invalid sender or recipient record");
            return;
        }

        float senderAmount= sender.getBalance();
        float recipientAmount=recipient.getBalance();
        float amount=(transaction.getAmount());

        if(senderAmount<amount){
            System.out.println("Invalid sender amount");
            return;

        }
        recipient.setBalance(recipientAmount+amount+ transaction.getIncentive());
        sender.setBalance(senderAmount-amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord transactionRecord=new TransactionRecord(sender,recipient,amount);
        transactionRepository.save(transactionRecord);

        System.out.println("Transaction completed");




    }
}
