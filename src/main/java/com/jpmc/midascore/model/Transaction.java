package com.jpmc.midascore.model;

public class Transaction {

    private String id;
    private double amount;
    private String type;
    private String description;

    public Transaction() {
    }
    
    public Transaction(String id, double amount, String type, String description) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
