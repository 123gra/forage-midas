package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.Balance;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final UserRepository userRecordRepository;

    public BalanceController(UserRepository userRecordRepository) {
        this.userRecordRepository = userRecordRepository;
    }

    @GetMapping
    public Balance getBalance(@RequestParam("userId") Long userId) {
        return userRecordRepository.findById(userId)
                .map(user -> new Balance())
                .orElse(new Balance());
    }
}
