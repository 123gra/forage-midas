package com.jpmc.midascore;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        // fetch user by id and return balance or zero if not found
        UserRecord user = userRepository.findById(userId);
        float amount = (user != null ? user.getBalance() : 0f);
        return new Balance(amount);
    }
} 