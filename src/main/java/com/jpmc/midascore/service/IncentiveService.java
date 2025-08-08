package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IncentiveService {

    // This simulates a database
    private final Map<Long, Double> userAmounts = new ConcurrentHashMap<>();

    public void applyIncentive(Long userId, Double amount) {
        userAmounts.put(userId, userAmounts.getOrDefault(userId, 0.0) + amount);
    }

    public Double getAmount(Long userId) {
        return userAmounts.getOrDefault(userId, 0.0);
    }
}