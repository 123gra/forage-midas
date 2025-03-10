package com.jpmc.midascore.kafka;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class KafkaConsumer {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public TransactionRepository transactionRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(ConsumerRecord<String, Transaction> record) {
        Transaction transaction = record.value();
        Optional<UserRecord> senderOpt = Optional.ofNullable(userRepository.findById(transaction.getSenderId()));
        Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(transaction.getRecipientId()));

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            if (sender.getBalance() >= transaction.getAmount()) {
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount());

                userRepository.save(sender);
                userRepository.save(recipient);

                TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
                transactionRepository.save(transactionRecord);

                System.out.println("Transaction recorded - FROM:" + sender.getName() + " TO:" + recipient.getName() + " SENDER_BALANCE: " + sender.getBalance() + " RECIPIENT_BALANCE: " + recipient.getBalance());
            } else {
                System.out.println("Transaction failed: Insufficient balance for sender ID " + sender.getId());
            }
        } else {
            System.out.println("Transaction failed: Invalid sender or recipient ID.");
        }
    }

    public ConsumerFactory<String, Transaction> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "midas-core-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.jpmc.midascore.foundation");
        return new DefaultKafkaConsumerFactory<>(props);
    }
}