package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    private final UserRepository users;

    public BalanceController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        // use your non-Optional finder:
        UserRecord u = users.findById(userId);
        float bal = (u != null) ? u.getBalance() : 0f;
        // **pass** the userId and the computed balance into the Balance ctor:
        return new Balance(bal);
    }

}
