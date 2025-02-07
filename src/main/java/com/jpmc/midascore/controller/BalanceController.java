package com.jpmc.midascore.controller;

import com.jpmc.midascore.model.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        Optional<UserRecord> userOpt = userRepository.findById(userId);
        float balance = userOpt.map(UserRecord::getBalance).orElse(0.0f);
        return new Balance(balance);
    }
}
