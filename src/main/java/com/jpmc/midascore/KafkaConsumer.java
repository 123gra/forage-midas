package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.model.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaConsumer {

    private final List<Transaction> receivedTransactions = new ArrayList<>();
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public KafkaConsumer(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void consume(Transaction transaction) {
        receivedTransactions.add(transaction);

        try {
            long senderId = Long.parseLong(transaction.getId());
            long recipientId = (long) transaction.getAmount();
            double amount = Double.parseDouble(transaction.getType());

            UserRecord sender = userRepository.findById(senderId);
            UserRecord recipient = userRepository.findById(recipientId);

            if (sender == null || recipient == null) {
                return;
            }

            if (sender.getBalance() < amount) {
                return;
            }

            sender.setBalance((float) (sender.getBalance() - amount));
            recipient.setBalance((float) (recipient.getBalance() + amount));

            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, amount);
            transactionRepository.save(record);
        } catch (Exception ignored) {
            // ignore malformed transaction messages
        }
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }
}
