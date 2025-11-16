package com.jpmc.midascore.controller;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    private DatabaseConduit databaseConduit;

    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance(@RequestParam Long userId) {
        logger.info("Balance query requested for userId: {}", userId);

        UserRecord user = databaseConduit.findUserById(userId);
        if (user == null) {
            logger.warn("User not found for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Balance balance = new Balance(user.getBalance());
        logger.info("Returning balance: {} for userId: {}", balance.getAmount(), userId);
        return ResponseEntity.ok(balance);
    }
}