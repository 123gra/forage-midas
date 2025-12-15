package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance(@RequestParam Long userId){
        return ResponseEntity.ok().body(balanceService.queryUserBalance(userId));
    }

}
