package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
    private final UserRepository userRepository;

    public TransactionConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "test")
    @Transactional
    public void processTransaction(String message) {
        try {
            logger.info("Received transaction: {}", message);
            String[] parts = message.split(",");
            if (parts.length != 3) {
                logger.error("Invalid transaction format: {}", message);
                return;
            }

            long fromId = Long.parseLong(parts[0].trim());
            long toId = Long.parseLong(parts[1].trim());
            double amount = Double.parseDouble(parts[2].trim());

            UserRecord fromUser = userRepository.findById(fromId);
            UserRecord toUser = userRepository.findById(toId);

            if (fromUser != null && toUser != null) {
                fromUser.setBalance(fromUser.getBalance() - amount);
                toUser.setBalance(toUser.getBalance() + amount);
                userRepository.save(fromUser);
                userRepository.save(toUser);
                logger.info("Transaction processed: {} -> {} amount: {}", fromId, toId, amount);
            } else {
                logger.error("User not found: from={}, to={}", fromId, toId);
            }
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", message, e);
        }
    }
}