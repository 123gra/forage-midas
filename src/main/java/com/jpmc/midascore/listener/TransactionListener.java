package com.jpmc.midascore.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private int counter = 0;

    public TransactionListener(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(String message) throws JsonProcessingException {
        Transaction transaction = objectMapper.readValue(message, Transaction.class);
        transactionService.process(transaction);

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        System.out.println("Received transaction: sender=" +
                (sender != null ? sender.getName() : transaction.getSenderId()) +
                ", recipient=" +
                (recipient != null ? recipient.getName() : transaction.getRecipientId()) +
                ", amount=" + transaction.getAmount() + ", senderBalance=" + sender.getBalance() + ", recipientBalance=" + recipient.getBalance());

        counter++;
        if (counter <= 4) {
            System.out.println("Transaction #" + counter + " amount = " + transaction.getAmount());
        }
    }
}
