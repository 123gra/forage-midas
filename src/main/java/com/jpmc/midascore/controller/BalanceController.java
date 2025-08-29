package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /balance?userId=123
    @GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public Balance getBalance(@RequestParam("userId") long userId) {
        if (userId <= 0) {
            return new Balance(0f);
        }
        UserRecord user = userRepository.findById(userId); // returns entity directly in your repo
        if (user == null) {
            return new Balance(0f);
        }
        return new Balance(user.getBalance());
    }
}
