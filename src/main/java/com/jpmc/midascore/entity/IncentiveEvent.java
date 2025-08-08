package com.jpmc.midascore.entity;

public class IncentiveEvent {
    private Long userId;
    private Double amount;

    // Constructors
    public IncentiveEvent() {}

    public IncentiveEvent(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}