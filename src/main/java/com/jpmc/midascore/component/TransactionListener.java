package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Service
public class TransactionListener {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void consume(Transaction transaction) {
        System.out.println("✅ Received Transaction: " + transaction);

        // Step 1: Call incentive API
    // String url = "http://localhost:8080/incentive";
// ResponseEntity<Incentive> response = restTemplate.postForEntity(url, transaction, Incentive.class);
// Incentive incentive = response.getBody();
// double incentiveAmount = (incentive != null) ? incentive.getAmount() : 0.0;

// Temporary: no incentive
double incentiveAmount = 0.0;


        // Step 2: Update recipient’s balance (add incentive)
        Optional<UserRecord> recipientOpt = Optional.of(userRepository.findById(transaction.getRecipientId()));
        if (recipientOpt.isPresent()) {
            UserRecord recipient = recipientOpt.get();
            float newBalance = recipient.getBalance() + transaction.getAmount() + (float) incentiveAmount;
            recipient.setBalance(newBalance);
            userRepository.save(recipient);
        }

        // Step 3: Deduct only transaction amount from sender (NOT incentive)
        Optional<UserRecord> senderOpt = Optional.of(userRepository.findById(transaction.getSenderId()));
        if (senderOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            float newBalance = sender.getBalance() - transaction.getAmount();
            sender.setBalance(newBalance);
            userRepository.save(sender);
        }

        System.out.println("💰 Incentive applied: " + incentiveAmount);
    }
}
