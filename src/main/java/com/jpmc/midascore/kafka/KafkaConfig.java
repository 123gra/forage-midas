package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // primary property we try (set by EmbeddedKafkaBroker in tests)
    // spring.embedded.kafka.brokers is set by spring-kafka-test at runtime for tests
    @Value("${spring.embedded.kafka.brokers:}")
    private String embeddedBrokers; // will be empty when not running embedded tests

    // optional env variable or system property fallback (if you set KAFKA_BOOTSTRAP_SERVERS)
    @Value("${KAFKA_BOOTSTRAP_SERVERS:}")
    private String envBootstrap;

    // final bootstrap servers to use
    private String determineBootstrapServers() {
        if (embeddedBrokers != null && !embeddedBrokers.isBlank()) {
            return embeddedBrokers;
        }
        if (envBootstrap != null && !envBootstrap.isBlank()) {
            return envBootstrap;
        }
        // final fallback (matches the EmbeddedKafka broker config used by tests)
        return "localhost:9092";
    }

    // -----------------------
    // ProducerFactory + KafkaTemplate (Transaction-valued)
    // -----------------------
    @Bean
    public ProducerFactory<String, Transaction> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        String bootstrapServers = determineBootstrapServers();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Transaction> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // -----------------------
    // ConsumerFactory + Listener Container (Transaction-valued)
    // -----------------------
    @Bean
    public ConsumerFactory<String, Transaction> consumerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();

        String bootstrapServers = determineBootstrapServers();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "midas-core-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<Transaction> jsonDeserializer = new JsonDeserializer<>(Transaction.class);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListenerContainerFactory(Environment env) {
        ConcurrentKafkaListenerContainerFactory<String, Transaction> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(env));
        return factory;
    }
}
