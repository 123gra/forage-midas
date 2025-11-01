package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        if (userId == null) {
            return new Balance(0f);
        }
        Optional<UserRecord> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            return new Balance(0f);
        }
        return new Balance(opt.get().getBalance());
    }
}