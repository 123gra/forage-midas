package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KafkaConsumer {

    @Autowired
    private RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    public KafkaConsumer(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository){
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction){
        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient= userRepository.findById(recipientId);

        if(sender!=null && recipient!=null && sender.getBalance() >=amount){

            sender.setBalance(sender.getBalance()- amount);
            recipient.setBalance(recipient.getBalance() + amount);

            Incentive incentive = restTemplate.postForObject(
                    "http://localhost:8080/incentive",
                    transaction,
                    Incentive.class
            );
            float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;
            recipient.setBalance(recipient.getBalance() + incentiveAmount);
            userRepository.save(sender);
            userRepository.save(recipient);
            if ("wilbur".equals(sender.getName()) || "wilbur".equals(recipient.getName())) {
                System.out.println("Wilbur balance (update): " + ("wilbur".equals(sender.getName()) ? sender.getBalance() : recipient.getBalance()));
                logger.info("✅ Received Transaction: {}", transaction.getAmount());
            }

            TransactionRecord record = new TransactionRecord(sender,recipient,amount, incentiveAmount);
            transactionRecordRepository.save(record);
        }

    }
}


