package com.jpmc.midascore.listener;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaTransactionListener {
    private final TransactionService svc;

    public KafkaTransactionListener(TransactionService svc) {
        this.svc = svc;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTransaction(Transaction t) {
        svc.handle(t);
    }
}
