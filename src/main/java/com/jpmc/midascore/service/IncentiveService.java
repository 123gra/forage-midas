package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;

@Service
public class IncentiveService {

    public double calculateIncentive(double amount) {
        // Simple fixed incentive for testing
        return amount * 0.01; // 1% incentive
    }
}