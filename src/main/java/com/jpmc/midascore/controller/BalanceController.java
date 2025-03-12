package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jpmc.midascore.entity.UserRecord;

import java.util.Optional;

@RestController
public class BalanceController {

    @Autowired
    public UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {

        Optional<UserRecord> userOpt = userRepository.findById(userId);
        float balance = userOpt.map(UserRecord::getBalance).orElse(0.0f);
        return new Balance(balance);
    }
}
