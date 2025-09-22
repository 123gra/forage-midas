package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        logger.info("Querying balance for user ID: {}", userId);
        
        try {
            var user = userService.findUserById(userId);
            if (user != null) {
                logger.info("Found user {} with balance: {}", user.getName(), user.getBalance());
                return new Balance(user.getBalance());
            } else {
                logger.info("User with ID {} not found, returning balance 0", userId);
                return new Balance(0.0f);
            }
        } catch (Exception e) {
            logger.error("Error querying balance for user ID {}: {}", userId, e.getMessage());
            return new Balance(0.0f);
        }
    }
}
