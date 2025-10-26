package com.jpmc.midascore.foundation;

import java.io.Serializable;

public class Incentive implements Serializable {

    private long userId;
    private double amount; // ✅ This is the missing field

    public Incentive() {
    }

    public Incentive(long userId, double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getAmount() {  // ✅ This getter is required
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Incentive{" +
                "userId=" + userId +
                ", amount=" + amount +
                '}';
    }
}
