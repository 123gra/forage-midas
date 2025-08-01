package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        logger.info("Received balance request for userId: {}", userId);

        Optional<UserRecord> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            UserRecord user = userOpt.get();
            float userBalance = user.getBalance();
            logger.info("Found user {} with balance: {}", user.getName(), userBalance);
            return new Balance(userBalance);
        } else {
            logger.info("User with id {} not found, returning balance 0", userId);
            return new Balance(0);
        }
    }
}