package com.jpmc.midascore.kafka;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {

    private final DatabaseConduit databaseConduit;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(
            DatabaseConduit databaseConduit,
            TransactionRepository transactionRepository,
            RestTemplate restTemplate
    ) {
        this.databaseConduit = databaseConduit;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group"
    )
    public void listen(Transaction transaction) {

        var sender = databaseConduit.findUser(transaction.getSenderId());
        var recipient = databaseConduit.findUser(transaction.getRecipientId());

        // Validate users
        if (sender == null || recipient == null) {
            return;
        }

        // Validate balance
        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // 🔹 Call Incentive API
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class
        );

        double incentiveAmount =
                incentive != null ? incentive.getAmount() : 0.0;

        // 🔹 Update balances
        sender.setBalance(
        (float) (sender.getBalance() - transaction.getAmount())
);

recipient.setBalance(
        (float) (recipient.getBalance()
                + transaction.getAmount()
                + incentiveAmount)
);


        // 🔹 Save transaction with incentive
        TransactionRecord record =
                new TransactionRecord(
                        transaction.getAmount(),
                        incentiveAmount,
                        sender,
                        recipient
                );

        transactionRepository.save(record);

       }
}
