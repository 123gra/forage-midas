package com.jpmc.midascore;

// Task 4

import java.math.BigDecimal;

public class Incentive {
    private BigDecimal amount;

    public Incentive(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal amount() {
        return amount;
    }
}

