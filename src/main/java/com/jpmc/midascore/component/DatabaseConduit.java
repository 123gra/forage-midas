package com.jpmc.midascore.component;

import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {
    // Simple version without Kafka dependencies
    public void saveTransaction(String transaction) {
        System.out.println("Saving transaction: " + transaction);
    }
}