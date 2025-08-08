package com.jpmc.midascore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.IncentiveEvent;
import com.jpmc.midascore.service.IncentiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IncentiveConsumer {

    @Autowired
    private IncentiveService incentiveService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "incentives", groupId = "incentive-consumers")
    public void consume(String message) {
        try {
            IncentiveEvent event = objectMapper.readValue(message, IncentiveEvent.class);
            System.out.println("Received Incentive for User " + event.getUserId() + ": " + event.getAmount());
            incentiveService.applyIncentive(event.getUserId(), event.getAmount());
        } catch (Exception e) {
            System.err.println("Error parsing message: " + message);
            e.printStackTrace();
        }
    }
}