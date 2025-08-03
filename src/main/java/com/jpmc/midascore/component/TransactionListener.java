package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @KafkaListener(
            topics = "${kafka.topic.transaction}",
            groupId = "midas-core-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(Transaction transaction) {
        System.out.println(transaction);
    }

}
