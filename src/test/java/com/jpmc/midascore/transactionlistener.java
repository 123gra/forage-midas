package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
class TransactionListener {

    @KafkaListener(topics = "${kafka.topic}", groupId = "midas-core")
    public void listen(Transaction transaction) {
        System.out.println("Received: " + transaction);
        // <-- put breakpoint here
    }
}
@Service
public class transactionlistener {

    @KafkaListener(topics = "${kafka.topic}", groupId = "midas-core")
    public void listen(Transaction transaction) {
        System.out.println("Received: " + transaction);
        // <-- put breakpoint here
    }
}
