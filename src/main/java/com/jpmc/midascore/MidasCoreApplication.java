package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@SpringBootApplication
public class MidasCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MidasCoreApplication.class, args);
    }

    @Value("${general.kafka-topic}")  // This will get "my-midas-topic"
    private String kafkaTopicName;

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, Transaction> kafkaTemplate) {
        return args -> {
            Transaction transaction = new Transaction();
            kafkaTemplate.send(kafkaTopicName, transaction);
            System.out.println("✅ Sent transaction: " + transaction);
        };
    }
}
