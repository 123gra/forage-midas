package com.jpmc.midascore.kafka;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.model.Incentive;
import com.jpmc.midascore.model.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

@Component
public class TransactionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionListener.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${kafka.topic.transactions}")
    @Transactional
    public void receive(Transaction transaction) {
        LOG.info("Received transaction: {}", transaction);

        Optional<UserRecord> senderOpt = userRepository.findById(Long.parseLong(transaction.getAccountId()));
        Optional<UserRecord> recipientOpt = userRepository.findById(Long.parseLong(transaction.getType()));

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            if (sender.getBalance() >= transaction.getAmount().floatValue()) {
                Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);

                sender.setBalance(sender.getBalance() - transaction.getAmount().floatValue());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount().floatValue() + incentive.getAmount().floatValue());

                userRepository.save(sender);
                userRepository.save(recipient);

                TransactionRecord transactionRecord = new TransactionRecord(
                        sender,
                        recipient,
                        transaction.getAmount(),
                        Instant.parse(transaction.getTimestamp()),
                        incentive.getAmount()
                );
                transactionRepository.save(transactionRecord);
                LOG.info("Transaction processed and saved with incentive.");
            } else {
                LOG.warn("Insufficient funds. Discarding transaction: {}", transaction);
            }
        } else {
            LOG.warn("Sender or recipient not found. Discarding transaction: {}", transaction);
        }
    }
}
