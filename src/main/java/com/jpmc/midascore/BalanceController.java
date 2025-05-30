package com.jpmc.midascore;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import java.util.List;
import java.util.ArrayList;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        // load all users in insertion order and use userId as index
        List<UserRecord> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        int idx = (int) userId;
        // if userId index is out of range, return zero balance
        float amount = 0f;
        if (idx >= 0 && idx < users.size()) {
            amount = users.get(idx).getBalance();
        }
        return new Balance(amount);
    }
} 