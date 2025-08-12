package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void consume(ConsumerRecord<String, Transaction> record){
        Transaction transaction = record.value();
        System.out.println("Transaction: " + transaction);
    }
}
