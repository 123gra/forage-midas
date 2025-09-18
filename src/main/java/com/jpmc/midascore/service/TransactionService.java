package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate(); 
    }

    @Transactional
    public boolean processTransaction(Transaction tx) {
        // Validate sender
        Optional<UserRecord> senderOpt = userRepository.findById(tx.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return false; // invalid sender or recipient
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < tx.getAmount()) {
            return false; // insufficient balance
        }

        // Call incentives API
        Incentive incentive = restTemplate.postForObject(
            "http://localhost:8080/incentive",
            tx,
            Incentive.class
        );
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0;


        // Adjust balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);

        // Save changes
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, tx.getAmount(), incentiveAmount);
        transactionRepository.save(record);

        UserRecord myuser  = new UserRecord(null, 0);

        if(sender.getName().equals("wilbur")){
            myuser = sender;
        }
        else if(recipient.getName().equals("wilbur")){
            myuser = recipient;
        }

        System.out.println("ans : " + myuser.getBalance());
        

        return true;
    }
}
