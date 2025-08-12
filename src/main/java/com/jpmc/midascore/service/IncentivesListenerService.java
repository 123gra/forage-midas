package com.jpmc.midascore.service;
import com.jpmc.midascore.controller.BalanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class IncentivesListenerService {

    @Autowired
    private BalanceController balanceController;

    @KafkaListener(topics = "incentives", groupId = "midas-group")
    public void handleIncentive(String message) {
        // Example message format: {"userId":"12345","amount":500}

        // Parse the message (here using simple string parsing; in real use JSON parser)
        String userId = message.split("\"userId\":\"")[1].split("\"")[0];
        float newBalance = Float.parseFloat(message.split("\"amount\":")[1].replace("}", "").trim());

        // Update balance via controller
        balanceController.updateBalance(userId, newBalance);

        System.out.println("Updated balance for user: " + userId + " by amount: " + newBalance);
    }
}