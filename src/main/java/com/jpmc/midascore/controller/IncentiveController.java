package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incentive")
public class IncentiveController {

    @PostMapping
    public Incentive calculateIncentive(@RequestBody Transaction transaction) {
        double amount = transaction.getAmount();
        double incentiveAmount = 1.0;

        if (amount > 100) {
            incentiveAmount = amount * 0.05; // 5% incentive for >100
        } else if (amount > 50) {
            incentiveAmount = amount * 0.02; // 2% incentive for >50
        } else {
            incentiveAmount = amount * 0.01; // 1% incentive otherwise
        }

        System.out.println("🎯 Calculated Incentive: " + incentiveAmount);

        return new Incentive(transaction.getRecipientId(), incentiveAmount);
    }
}
