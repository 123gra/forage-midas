package com.jpmc.midascore.foundation;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Value("${midas.topic:midas-topic}")
    private String topic;

    @Autowired
    public TransactionListener(UserRepository userRepository,
                               TransactionRepository transactionRepository,
                               RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(
            topics = "${midas.topic}",
            groupId = "midas-core-group",
            containerFactory = "transactionKafkaListenerFactory"
    )
    public void listen(Transaction transaction, ConsumerRecord<String, String> record) {
        System.out.println("Received transaction: " + transaction);

        Long senderId = transaction.getSenderId();
        Long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            System.out.println("❌ Invalid sender or recipient — transaction discarded.");
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < amount) {
            System.out.println("❌ Insufficient balance — transaction discarded.");
            return;
        }

        // 💰 Call Incentives API
        float incentiveAmount = 0.0f;
        try {
            ResponseEntity<Incentive> response = restTemplate.postForEntity(
                    "http://localhost:8080/incentive",
                    transaction,
                    Incentive.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                incentiveAmount = response.getBody().getAmount();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to fetch incentive: " + e.getMessage());
        }

        // ✅ Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // 💾 Record the transaction
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(transactionRecord);

        System.out.println("✅ Transaction processed. Sender: " + sender.getName() +
                ", Recipient: " + recipient.getName() +
                ", Amount: " + amount +
                ", Incentive: " + incentiveAmount);

        userRepository.findByName("wilbur").ifPresent(user ->
                System.out.println("ℹ️ Wilbur’s final balance (rounded down): " + (int) user.getBalance()));
    }
}
