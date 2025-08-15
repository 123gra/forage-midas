package com.jpmc.midascore;

public class Transaction {
    private double amount;
    private String id;

    // Proper constructor
    public Transaction(long id1, long id2, float amount) {
        this.id = id1 + "-" + id2;   // or any unique combination of the two longs
        this.amount = amount;
    }

    // Default constructor (needed for deserialization)
    public Transaction() {}

    // Getters and setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "Transaction{id='" + id + "', amount=" + amount + "}";
    }
}