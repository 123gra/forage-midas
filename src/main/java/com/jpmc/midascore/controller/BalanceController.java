package com.jpmc.midascore.controller;


import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class BalanceController {

    // Example in-memory storage for balances (replace with actual service logic)
    private final Map<String, Float> balances = new ConcurrentHashMap<>();

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam String userId) {
        float amount = balances.getOrDefault(userId, 0.0f);
        return new Balance(amount);
    }

    // Optional: method for Kafka listener to update balances
    public void updateBalance(String userId, float newBalance) {
        balances.put(userId, newBalance);
    }
}
