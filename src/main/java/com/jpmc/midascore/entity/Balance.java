package com.jpmc.midascore.entity;

public class Balance {
    private String userId;
    private double balance;

    public Balance(String userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Balance{" + "userId='" + userId + '\'' + ", balance=" + balance + '}';
    }
}