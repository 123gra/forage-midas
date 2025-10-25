package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class KafkaListenerComponent {

    private static final Logger log = LoggerFactory.getLogger(KafkaListenerComponent.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    @Transactional
    public void listen(Transaction transaction) {
        log.info("Received Transaction: {}", transaction);

        Optional<UserRecord> senderOptional = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOptional = userRepository.findById(transaction.getRecipientId());

        if (senderOptional.isEmpty()) {
            log.warn("Invalid senderId: {}", transaction.getSenderId());
            return;
        }
        if (recipientOptional.isEmpty()) {
            log.warn("Invalid recipientId: {}", transaction.getRecipientId());
            return;
        }

        UserRecord sender = senderOptional.get();
        UserRecord recipient = recipientOptional.get();
        BigDecimal amount = BigDecimal.valueOf(transaction.getAmount());

        if (sender.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance for sender {}. Current balance: {}, Transaction amount: {}",
                    sender.getName(), sender.getBalance(), amount);
            return;
        }

        // Update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));

        // Call Incentive API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);
        Incentive incentiveResponse = restTemplate.postForObject("http://localhost:8080/incentive", request, Incentive.class);

        BigDecimal incentiveAmount = BigDecimal.ZERO;
        if (incentiveResponse != null && incentiveResponse.getAmount() != null) {
            incentiveAmount = incentiveResponse.getAmount();
            recipient.setBalance(recipient.getBalance().add(incentiveAmount)); // Add incentive to recipient
        }

        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        TransactionRecord transactionRecord = new TransactionRecord(
                sender,
                recipient,
                amount,
                LocalDateTime.now(),
                incentiveAmount
        );
        transactionRepository.save(transactionRecord);

        log.info("Transaction recorded and balances updated for sender {} and recipient {}",
                sender.getName(), recipient.getName());
    }
}
