package com.jpmc.midascore.entity;

public class Balance {
    private float balance;

    public Balance() {
        this.balance = 0;
    }

    public Balance(float balance) {
        this.balance = balance;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "balance=" + balance +
                '}';
    }
}