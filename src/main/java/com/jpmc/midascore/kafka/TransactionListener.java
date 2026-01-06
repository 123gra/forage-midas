package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {


    /**
     * This method is invoked automatically whenever a message
     * arrives at the configured Kafka topic.
     */
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-consumer"
    )
    public void consume(Transaction transaction) {

        // For Task Two:
        // DO NOTHING with the transaction
        // The test debugger will inspect this object

        System.out.println(
                "Received transaction with amount: " + transaction.getAmount()
        );
    }
}
