package com.jpmc.midascore.entity;

public class Incentive {
    private Double amount;

    // Default constructor
    public Incentive() {}

    // Constructor
    public Incentive(Double amount) {
        this.amount = amount;
    }

    // Getter and setter
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}