package com.jpmc.midascore.kafka;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.jpmc.midascore.foundation.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TransactionListener {
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(ConsumerRecord<String, Transaction> record) {
        System.out.println("Received message: " + record.value());
    }


}