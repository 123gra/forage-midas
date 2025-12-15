package com.jpmc.midascore.service;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final DatabaseConduit databaseConduit;

    public Balance queryUserBalance(Long userId) {
        Balance balance = new Balance();
        balance.setAmount(databaseConduit.queryUserBalance(userId));
        return balance;
    }
}
