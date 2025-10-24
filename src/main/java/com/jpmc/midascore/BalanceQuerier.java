package com.jpmc.midascore;

import com.jpmc.midascore.entity.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceQuerier {
    public Balance query(Long userId) {
        return new Balance(String.valueOf(userId), 0.0);
    }
}