package com.jpmc.midascore;

import com.jpmc.midascore.dto.TransactionDTO;
import com.jpmc.midascore.entity.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class TransactionConsumerTest {

    @Container
    private static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3"));

    @DynamicPropertySource
    static void setKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.consumer.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "midas-test-group");
    }

    @Autowired
    private KafkaTemplate<String, TransactionDTO> kafkaTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testConsumeTransaction() {

        String topic = "transactions";
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAccountId("test-kafka-account");
        transactionDTO.setAmount(new BigDecimal("250.75"));

        ProducerRecord<String, TransactionDTO> record = new ProducerRecord<>(topic, transactionDTO);
        record.headers().add("__TypeId__", "transaction".getBytes(StandardCharsets.UTF_8));


        kafkaTemplate.send(record);


        await().atMost(Duration.ofSeconds(10)).until(() -> !transactionRepository.findAll().isEmpty());

        List<Transaction> transactions = transactionRepository.findAll();
        Transaction savedTransaction = transactions.get(0);

        assertEquals(1, transactions.size());
        assertEquals(transactionDTO.getAccountId(), savedTransaction.getAccountId());
        assertEquals(0, transactionDTO.getAmount().compareTo(savedTransaction.getAmount()));
        assertNotNull(savedTransaction.getTimestamp());
    }
}