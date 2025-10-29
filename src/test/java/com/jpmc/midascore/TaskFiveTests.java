package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Balance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test",
        "spring.kafka.producer.retries=0"
    }
)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {
    "listeners=PLAINTEXT://localhost:0", 
    "port=0",
    "num.partitions=1",
    "auto.create.topics.enable=true",
    "offsets.topic.replication.factor=1",
    "transaction.state.log.replication.factor=1",
    "transaction.state.log.min.isr=1",
    "group.initial.rebalance.delay.ms=0"
})
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.properties.enable.idempotence=false",
    "spring.kafka.producer.properties.max.in.flight.requests.per.connection=1",
    "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "spring.kafka.consumer.properties.isolation.level=read_committed"
})
public class TaskFiveTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFiveTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private BalanceQuerier balanceQuerier;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    private void waitForKafka() {
        int maxRetries = 5;
        int retries = 0;
        while (retries < maxRetries) {
            try {
                kafkaAdmin.describeTopics("test-topic");
                return;
            } catch (Exception e) {
                retries++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for Kafka", ie);
                }
            }
        }
        throw new RuntimeException("Kafka not ready after " + maxRetries + " retries");
    }


    @Test
    void task_five_verifier() throws InterruptedException {
        logger.info("Starting task_five_verifier test");
        waitForKafka();
        logger.info("Kafka is ready, populating users");
        
        userPopulator.populate();
        logger.info("Users populated, loading transaction lines");
        
        String[] transactionLines = fileLoader.loadStrings("/test_data/rueiwoqp.tyruei");
        logger.info("Loaded {} transaction lines", transactionLines.length);
        
        for (String transactionLine : transactionLines) {
            logger.info("Sending transaction: {}", transactionLine);
            kafkaProducer.send(transactionLine);
            Thread.sleep(200); // Increased delay between messages
        }
        
        logger.info("All transactions sent, waiting for processing");
        Thread.sleep(5000); // Increased wait time for processing

        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("submit the following output to complete the task (include begin and end output denotations)");
        StringBuilder output = new StringBuilder("\n").append("---begin output ---").append("\n");
        for (int i = 0; i < 13; i++) {
            Balance balance = balanceQuerier.query((long) i);
            output.append(balance.toString()).append("\n");
        }
        output.append("---end output ---");
        logger.info(output.toString());
    }
}
