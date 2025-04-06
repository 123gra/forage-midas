package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final UserRepository userRepository;

    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Balance getBalance(@RequestParam Long userId) {
        return userRepository.findById(userId)
                .map(user -> new Balance(user.getBalance()))
                .orElse(new Balance(0f));
    }
}