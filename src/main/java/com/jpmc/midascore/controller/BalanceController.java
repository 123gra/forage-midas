package com.jpmc.midascore.controller;

import com.jpmc.midascore.component.BalanceQuerier;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    
    @Autowired
    private BalanceQuerier balanceQuerier;
    
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        return balanceQuerier.query(userId);
    }
}
