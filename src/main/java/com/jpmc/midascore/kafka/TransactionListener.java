package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {


    private final TransactionProcessor processor;

    public TransactionListener(TransactionProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-consumer"
    )
    public void consume(Transaction transaction) {
        processor.process(transaction);
    }
}
