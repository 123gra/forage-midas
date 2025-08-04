package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
            topics = "${kafka.topic.transaction}",
            groupId = "midas-core-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(Transaction transaction) {
        System.out.println(transaction);
        try {
            transactionService.processTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", e.getMessage(), e);
        }
    }

}
