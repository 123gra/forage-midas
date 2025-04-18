package com.jpmc.midascore.foundation;

public class Balance {
    private Long userId;
    private float balance;

    public Balance(Long userId, float balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public float getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
