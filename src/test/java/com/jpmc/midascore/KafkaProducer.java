package com.jpmc.midascore;

// 📦 Importing required Kafka and Spring components
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * 📤 KafkaProducer is responsible for publishing Transaction events to a Kafka topic.
 * It parses raw input and sends structured messages using KafkaTemplate.
 */
@Component
public class KafkaProducer
{
    // 🧭 Kafka topic name, injected from application properties
    private final String topic;

    // 🚚 KafkaTemplate used to send messages to the broker
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    /**
     * 🛠️ Constructor-based injection of topic name and KafkaTemplate.
     *
     * @param topic          the Kafka topic to publish to
     * @param kafkaTemplate  the Kafka client used for sending messages
     */
    public KafkaProducer(@Value("${general.kafka-topic}") String topic,
                         KafkaTemplate<String, Transaction> kafkaTemplate)
    {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 📦 Parses a raw transaction line and sends it as a Transaction object to Kafka.
     * Expected format: "senderId, recipientId, amount"
     *
     * @param transactionLine comma-separated transaction string
     */
    public void send(String transactionLine)
    {
        String[] transactionData = transactionLine.split(", ");
        Transaction transaction = new Transaction(
                Long.parseLong(transactionData[0]),
                Long.parseLong(transactionData[1]),
                new BigDecimal(transactionData[2])
        );
        kafkaTemplate.send(topic, transaction);
    }
}