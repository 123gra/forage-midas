package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionListener {
    private final UserRepository repo;
    private final DatabaseConduit conduit;

    public TransactionListener(UserRepository repo, DatabaseConduit conduit) {
        this.repo = repo;
        this.conduit = conduit;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    @Transactional
    public void handle(Transaction t) {
        UserRecord sender = repo.findById(t.getSenderId());
        sender.setBalance(sender.getBalance() - t.getAmount());
        conduit.save(sender);

        UserRecord recipient = repo.findById(t.getRecipientId());
        recipient.setBalance(recipient.getBalance() + t.getAmount());
        conduit.save(recipient);
    }
} 