package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
public class TransactionService {
    private final UserRepository userRepo;
    private final TransactionRepository txRepo;
    private final RestTemplate rest;

    public TransactionService(UserRepository userRepo,
                              TransactionRepository txRepo,
                              RestTemplate rest) {
        this.userRepo = userRepo;
        this.txRepo   = txRepo;
        this.rest = rest;
    }

    @Transactional
    public void handle(Transaction incoming) {
        UserRecord sender = userRepo.findById(incoming.getSenderId());
        UserRecord recipient = userRepo.findById(incoming.getRecipientId());

        float amount = incoming.getAmount();

        if (sender.getBalance() < amount) {
            return; // insufficient funds — drop it
        }

        // call incentives API
        String incentiveUrl = "http://localhost:8080/incentive";
        Incentive inc = rest.postForObject(incentiveUrl, incoming, Incentive.class);
        float bonus = (inc != null ? inc.getAmount() : 0f);

        // adjust balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount+bonus);

        // persist updates and record
        userRepo.save(sender);
        userRepo.save(recipient);
        txRepo.save(new TransactionRecord(sender, recipient, amount, bonus));
    }
}
