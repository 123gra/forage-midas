package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserRecordListener {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void consume(UserRecord userRecord) {
        System.out.println("✅ Received UserRecord: " + userRecord);
        // Debug here to check first 4 balances
    }
}
