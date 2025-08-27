package com.jpmc.midascore;

// Task 4

import java.math.BigDecimal;

public class Incentive {

    private BigDecimal amount;

    // Default constructor required for JSON deserialization
    public Incentive() {
    }

    // Constructor for manual creation
    public Incentive(BigDecimal amount) {
        this.amount = amount;
    }

    // Getter
    public BigDecimal getAmount() {
        return amount;
    }

    // Setter
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
